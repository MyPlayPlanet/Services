package net.myplayplanet.services.cache_new;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.NonNull;
import net.myplayplanet.services.schedule.IScheduledTask;
import net.myplayplanet.services.schedule.ScheduledTaskProvider;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cache<K extends Serializable, V extends Serializable> {
    private LoadingCache<K, Optional<V>> loadingCache;
    private Function<K, V> function;

    @Getter
    private String name;
    private Timer timer;
    private AbstractSaveProvider<K, V> saveProvider;

    /**
     * @param name     the name of the Cache that will be created.
     * @param function the Function that will be called when no entry was found in the local or the redis cache.
     *                 this also makes it possible to load from Sql etc.
     */
    protected Cache(@NonNull String name, @NonNull Function<K, V> function) {
        this.name = name;
        this.function = function;

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
        timer = new Timer();


        this.saveProvider.load().forEach(this::update);

        ScheduledTaskProvider.getInstance().register(saveProvider);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveAll();
            timer.cancel();
        }));
    }

    /**
     * calls the "saveAll" implementation from the AbstractSaveProvider and removes the values that where updated successfully.
     */
    private void saveAll() {
        System.out.println("execute scheduler:");
        for (K k : saveProvider.saveAll(saveProvider.getSavableEntries())) {
            System.out.println("updated " + k.toString() + "!");
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


    public V getget(@NonNull K key, Function<K, V> function) {
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
        loadingCache.put(key, Optional.of(value));
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

    /**
     * this Method will always load from redis because the local Cache expires, and therefore is not reliable.
     * @return
     */
    public List<V> getExistingValues() {
        reloadWholeCacheFromRedis();
        return loadingCache.asMap().values().stream().map(v -> v.orElse(null)).collect(Collectors.toList());
    }

    /**
     * reloads all things from redis.
     */
    public void reloadWholeCacheFromRedis() {
        HashMap<K, V> map = new HashMap<>();

        //todo fill HashMap with data from redis


        for (K k : map.keySet()) {
            // i only need to load this in the local Cache. there is no need to call the
            // "update" Method because it needs to be in redis to get to this part of the Code and what is in
            // redis does not need to be saved or written to redis.
            loadingCache.put(k, Optional.of(map.get(k)));
        }
    }

    private void updateRedis(@NonNull K key, @NonNull V value) {
        //todo put value in redis
        System.out.println("update " + key.toString() + " in redis");
    }

    private V getFromRedis(@NonNull K key) {
        //todo get value from redis
        V result = null;
        System.out.println("get " + key.toString() + " from redis. result: " + ((result == null) ? "not successful" : "successful"));
        return result;
    }
}
