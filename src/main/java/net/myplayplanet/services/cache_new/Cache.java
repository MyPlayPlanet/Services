package net.myplayplanet.services.cache_new;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.logger.Log;
import net.myplayplanet.services.schedule.ScheduledTaskProvider;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
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
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .refreshAfterWrite(8, TimeUnit.MINUTES)
                .build(new CacheLoader<K, Optional<V>>() {
                    public Optional<V> load(K key) {
                        V result = getFromRedis(key);

                        if (result == null) {
                            result = function.apply(key);
                        }

                        if (result != null) {
                            handleUpdate(key, result);
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

    /**
     * This method will try to get the value from local cache, if it is not there it will try and get it from
     * the redis cache, if it still is nowhere to be found it will execute the function defined on the constructor of this class
     *
     * @return the value that was found in the cache or generated from the function, null if that function returns null.
     */
    public V get(@NonNull K key, boolean forceReload) {
        if (forceReload) {
            V result = this.function.apply(key);

            if (result == null) {
                return null;
            }

            this.update(key, result);
            return result;
        }

        try {
            return loadingCache.get(key).orElse(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method will try to get the value from local cache, if it is not there it will try and get it from
     * the redis cache, if it still is nowhere to be found it will execute the function defined on the constructor of this class
     *
     * @return the value that was found in the cache or generated from the function, null if that function returns null.
     */
    public V get(@NonNull K key) {
        return get(key, false);
    }

    public V get(@NonNull K key, Function<K, V> function) {
        return get(key, false, function);
    }

    public V get(@NonNull K key, boolean forceReload, Function<K, V> function) {
        if (forceReload) {
            V result = this.function.apply(key);

            if (result == null) {
                return null;
            }

            this.update(key, result);
            return result;
        }

        try {
            return loadingCache.get(key, () -> {
                V result = getFromRedis(key);

                if (result == null) {
                    result = function.apply(key);
                }

                if (result != null) {
                    handleUpdate(key, result);
                }
                return Optional.ofNullable(result);
            }).orElse(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * this will call update the local cache and call {@link #handleUpdate(Serializable, Serializable)}
     */
    public void update(@NonNull K key, @NonNull V value) {
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
        reloadWholeCacheFromRedis();
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
        try {
            ConnectionManager.getInstance().getByteConnection().async().hdel(
                    this.getName().getBytes(),
                    ConnectionManager.getInstance().getByteConnection().async().hkeys(this.getName().getBytes()).get().toArray(new byte[0][])
            );
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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
    public void reloadWholeCacheFromRedis() {
        HashMap<K, V> map = new HashMap<>();

        try {
            ConnectionManager.getInstance().getByteConnection().async()
                    .hgetall(this.getName().getBytes()).get()
                    .forEach((key, value) ->
                            map.put(SerializationUtils.deserialize(key), SerializationUtils.deserialize(value))
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

    private void updateRedis(@NonNull K key, @NonNull V value) {
        ConnectionManager.getInstance().getByteConnection().async().hset(this.getName().getBytes(), SerializationUtils.serialize(key), SerializationUtils.serialize(value));
    }

    private V getFromRedis(@NonNull K key) {
        try {
            return SerializationUtils.deserialize(ConnectionManager.getInstance().getByteConnection().async().hget(this.getName().getBytes(), SerializationUtils.serialize(key)).get());
        } catch (InterruptedException | ExecutionException e) {
            Log.getLog(log).error(e, "Error while getting {key} from cache {name}.", key.toString(), this.getName());
            return null;
        }
    }
}
