package net.myplayplanet.services.cache_new;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.logger.Log;
import net.myplayplanet.services.schedule.ScheduledTaskProvider;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class Cache<K extends Serializable, V extends Serializable> {
    private LoadingCache<K, Optional<V>> loadingCache;
    private Function<K, V> function;
    private List<Consumer<CacheUpdateEvent>> updateEvents;

    @Getter
    private String name;
    private AbstractSaveProvider<K, V> saveProvider;

    /**
     * @param name     the name of the Cache that will be created.
     * @param function the Function that will be called when no entry was found in the local or the redis cache.
     *                 this also makes it possible to load from Sql etc.
     */
    public Cache(@NonNull String name, @NonNull Function<K, V> function) {
        this.name = name;
        this.function = function;
        this.updateEvents = new ArrayList<>();

        loadingCache = CacheBuilder.newBuilder()
                .expireAfterWrite(21, TimeUnit.MINUTES)
                .build(new CacheLoader<K, Optional<V>>() {
                    public Optional<V> load(K key) {
                        System.out.println("(default) get");
                        V result = getFromRedis(key);

                        if (result == null) {
                            System.out.println("(default) apply Function");
                            result = function.apply(key);
                            if (result != null) {
                                System.out.println("(default) function result = " + result.toString());
                                update(key, result);
                            }else {
                                System.out.println("(default) function result = null");
                            }
                        }else{
                            System.out.println("(default) get from redis = " + result.toString());
                        }

                        return Optional.ofNullable(result);
                    }
                });
    }

    /**
     * @param name         the name of the Cache that will be created.
     * @param function     the Function that will be called when no entry was found in the local or the redis cache.
     *                     this also makes it possible to load from Sql etc.
     * @param saveProvider the class that is used to save the cache entries.
     */
    public Cache(@NonNull String name, @NonNull Function<K, V> function, @NonNull AbstractSaveProvider<K, V> saveProvider) {
        this(name, function);
        this.saveProvider = saveProvider;

        this.saveProvider.load().forEach(this::update);

        ScheduledTaskProvider.getInstance().register(saveProvider);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> saveAll()));
    }

    /**
     * calls the "saveAll" implementation from the AbstractSaveProvider and removes the values that where updated successfully.
     */
    private void saveAll() {
        for (K k : saveProvider.saveAll(saveProvider.getSavableEntries())) {
            saveProvider.getSavableEntries().remove(k);
        }
    }

    public V get(@NonNull K key, boolean forceReload) {
        return getWithFunction(key, forceReload, null);
    }

    public V get(@NonNull K key) {
        return getWithFunction(key, false, null);
    }

    public V get(@NonNull K key, Function<K, V> function) {
        return get(key, false, function);
    }

    public V get(@NonNull K key, boolean forceReload, Function<K, V> function) {
        return getWithFunction(key, forceReload, function);
    }

    /**
     * This method will try to get the value from local cache, if it is not there it will try and get it from
     * the redis cache, if it still is nowhere to be found it will execute the function defined on the constructor of this class
     *
     * @return the value that was found in the cache or generated from the function, null if that function returns null.
     */
    private V getWithFunction(@NonNull K key, @NonNull boolean force, Function<K, V> function) {
        System.out.println("=========================");
        System.out.println("(getWithFunction) cache Name: " + this.getName());
        System.out.println("(getWithFunction) key: " + key.toString());
        System.out.println("(getWithFunction) force: " + force);
        System.out.println("(getWithFunction) function: " + ((function != null) ?  function.toString() : "null"));
        if (force) {
            V result = this.function.apply(key);

            if (result == null) {
                System.out.println("(getWithFunction) force result null");
                return null;
            }
            System.out.println("(getWithFunction) key " + key.toString());

            this.update(key, result);
            return result;
        }

        if (function != null) {
            System.out.println("(getWithFunction) function != null");
            try {
                return loadingCache.get(key, () -> {
                    V result = getFromRedis(key);
                    System.out.println("(getWithFunction) own function get");

                    if (result == null) {
                        System.out.println("(getWithFunction) own Function apply");
                        result = function.apply(key);
                        if (result != null) {
                            System.out.println("(getWithFunction) function == " +result.toString());
                            update(key, result);
                        }else {
                            System.out.println("(getWithFunction) function == " + null);
                        }
                    }else {
                        System.out.println("(getWithFunction) getFrom Redis Successful");
                    }
                    System.out.println("(getWithFunction) result: " +((result != null) ? new Gson().toJson(result) : "null"));
                    return Optional.ofNullable(result);
                }).orElse(null);
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            Optional<V> v = loadingCache.get(key);
            System.out.println("(getWithFunction) not own Function: " + ((v.isPresent()) ? new Gson().toJson(v.get()) : "null"));
            return v.orElse(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * this will call update the local cache and call {@link #handleUpdate(Serializable, Serializable)}
     */
    public void update(@NonNull K key,V value) {
        if (value == null) {
            System.out.println("update value is null");
            return;
        }

        CacheUpdateEvent<K, V> event = new CacheUpdateEvent(key, value);
        for (Consumer<CacheUpdateEvent> updateEvent : updateEvents) {
            updateEvent.accept(event);
        }

        if (event.isCancelled()) {
            return;
        }

        loadingCache.put(event.getKey(), Optional.of(event.getValue()));
        handleUpdate(key, value);
    }

    /**
     * Here the value and the key will be inserted into redis via the method {@link #updateRedis(Serializable, Serializable)}
     * and if a save provider exists it will put the date up to be saved in the save provider scheduler.
     */
    private void handleUpdate(@NonNull K key, @NonNull V value) {
        updateRedis(key, value);

        if (saveProvider != null) {
            saveProvider.getSavableEntries().put(key, value);
        }
    }

    public void registerUpdateEvent(Consumer<CacheUpdateEvent> updateEvent) {
        this.updateEvents.add(updateEvent);
    }

    /**
     * this Method will always load from redis because the local Cache expires, and therefore is not reliable.
     * @return
     */
    public List<V> getExistingValues() {
        reloadLocalCacheFromRedis();
        return loadingCache.asMap().values().stream().map(v -> v.orElse(null)).collect(Collectors.toList());
    }


    /**
     * clears the local cache and the redis cache and then executes the {@link AbstractSaveProvider#load()} Method.
     */
    public void reloadCache() {
        clearCache();
        if (saveProvider != null) {
            saveProvider.load();
        }
    }

    /**
     * clears local and redis cache.
     */
    public void clearCache() {
        clearRedisCache();
        clearLocalCache();
    }

    /**
     * clears the remote redis cache
     */
    public void clearRedisCache() {
        ConnectionManager.getInstance().getByteConnection().async().del(this.getName().getBytes());
    }

    /**
     * clears the local guava cache.
     */
    public void clearLocalCache() {
        loadingCache.invalidateAll();
    }

    /**
     * get every entry that is present in redis and loads it into the local cache.
     */
    public void reloadLocalCacheFromRedis() {
        HashMap<K, V> map = new HashMap<>();

        try {
            ConnectionManager.getInstance().getByteConnection().async()
                    .hgetall(this.getName().getBytes()).get()
                    .forEach((key, value) ->
                            {
                                CacheObject<V> deserialize = SerializationUtils.deserialize(value);
                                map.put(SerializationUtils.deserialize(key), deserialize.getValue());
                            }
                    );
        } catch (InterruptedException | ExecutionException e) {
            Log.getLog(log).error(e,"Error while reloading Cache {cache}", this.getName());
        }


        for (K k : map.keySet()) {
            // i only need to load this in the local Cache. there is no need to call the
            // "update" Method because it needs to be in redis to get to this part of the Code and what is in
            // redis does not need to be saved or written to redis.
            loadingCache.put(k, Optional.of(map.get(k)));
        }
    }

    private void updateRedis(@NonNull K key, @NonNull V v) {
        CacheObject<V> value = new CacheObject<>(new Date().getTime() + 3600, v);
        ConnectionManager.getInstance().getByteConnection().async().hset(this.getName().getBytes(), SerializationUtils.serialize(key), SerializationUtils.serialize(value));
    }

    private V getFromRedis(@NonNull K key) {
        System.out.println("get from redis for key "+ key.toString() + ":");
        try {
            byte[] keyAsByteArray = SerializationUtils.serialize(key);
            byte[] objectData = ConnectionManager.getInstance().getByteConnection().async().hget(this.getName().getBytes(), keyAsByteArray).get();

            if (objectData == null) {
                System.out.println("object data is null");
                return null;
            }

            CacheObject<V> value = SerializationUtils.deserialize(objectData);
            System.out.println("value.key: " + value.getLastModified() + "\t " + value.getValue().toString());

            //this makes is so that if the cache entry is older that one Hour it will be removed from redis and the cache is forced to reload it.
            if (new Timestamp(value.getLastModified()).before(new Timestamp(new Date().getTime()))) {
                System.out.println("expired");
                ConnectionManager.getInstance().getByteConnection().async().hdel(this.getName().getBytes(), keyAsByteArray);
                return null;
            }


            System.out.println("returned " + value.getValue().toString() +"!");
            return value.getValue();
        } catch (InterruptedException | ExecutionException e) {
            Log.getLog(log).error(e, "Error while getting {key} from cache {name}.", key.toString(), this.getName());
            return null;
        }
    }
}
