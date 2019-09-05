package net.myplayplanet.services.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.cache.providers.ICacheProvider;
import net.myplayplanet.services.cache.providers.MockProvider;
import net.myplayplanet.services.cache.providers.RedisProvider;
import net.myplayplanet.services.schedule.ScheduledTaskProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class Cache<K extends Serializable, V extends Serializable> {
    private LoadingCache<K, Optional<V>> localCache;
    private Function<K, V> function;
    private List<Consumer<CacheUpdateEvent>> updateEvents;
    private ICacheProvider<K, V> provider;

    @Getter
    private String name;
    private AbstractSaveProvider<K, V> saveProvider;

    @Getter
    @Setter
    private boolean saveAfterGet = true;

    /**
     * @param name             the name of the Cache that will be created.
     * @param redisCacheExpire the time in Seconds then the Redis Cache Should expire (default: 3600 sec.(1h))
     * @param function         the Function that will be called when no entry was found in the local or the redis cache.
     *                         this also makes it possible to load from Sql etc.
     * @param saveProvider     the class that is used to save the cache entries.
     */
    public Cache(String name, long redisCacheExpire, Function<K, V> function, AbstractSaveProvider<K, V> saveProvider) {
        this(name, 21, redisCacheExpire, function, saveProvider);
    }

    /**
     * @param name             the name of the Cache that will be created.
     * @param localCacheExpire the time in Minutes when the local Cache Expires. (default: 21 Min)
     * @param function         the Function that will be called when no entry was found in the local or the redis cache.
     *                         this also makes it possible to load from Sql etc.
     * @param saveProvider     the class that is used to save the cache entries.
     */
    public Cache(String name, int localCacheExpire, Function<K, V> function, AbstractSaveProvider<K, V> saveProvider) {
        this(name, localCacheExpire, 3600, function, saveProvider);
    }

    /**
     * @param name             the name of the Cache that will be created.
     * @param localCacheExpire the time in Minutes when the local Cache Expires. (default: 21 Min)
     * @param redisCacheExpire the time in Seconds then the Redis Cache Should expire (default: 3600 sec.(1h))
     * @param function         the Function that will be called when no entry was found in the local or the redis cache.
     *                         this also makes it possible to load from Sql etc.
     */
    public Cache(String name, int localCacheExpire, long redisCacheExpire, Function<K, V> function) {
        this(name, localCacheExpire, redisCacheExpire, function, null);
    }

    /**
     * @param name         the name of the Cache that will be created.
     * @param function     the Function that will be called when no entry was found in the local or the redis cache.
     *                     this also makes it possible to load from Sql etc.
     * @param saveProvider the class that is used to save the cache entries.
     */
    public Cache(String name, Function<K, V> function, AbstractSaveProvider<K, V> saveProvider) {
        this(name, 21, 3600, function, saveProvider);
    }

    /**
     * @param name             the name of the Cache that will be created.
     * @param redisCacheExpire the time in Seconds then the Redis Cache Should expire (default: 3600 sec.(1h))
     * @param function         the Function that will be called when no entry was found in the local or the redis cache.
     *                         this also makes it possible to load from Sql etc.
     */
    public Cache(String name, long redisCacheExpire, Function<K, V> function) {
        this(name, 21, redisCacheExpire, function, null);
    }

    /**
     * @param name             the name of the Cache that will be created.
     * @param localCacheExpire the time in Minutes when the local Cache Expires. (default: 21 Min)
     * @param function         the Function that will be called when no entry was found in the local or the redis cache.
     *                         this also makes it possible to load from Sql etc.
     */
    public Cache(String name, int localCacheExpire, Function<K, V> function) {
        this(name, localCacheExpire, 3600, function, null);
    }

    /**
     * @param name     the name of the Cache that will be created.
     * @param function the Function that will be called when no entry was found in the local or the redis cache.
     *                 this also makes it possible to load from Sql etc.
     */
    public Cache(String name, Function<K, V> function) {
        this(name, 21, 3600, function, null);
    }

    /**
     * @param name             the name of the Cache that will be created.
     * @param function         the Function that will be called when no entry was found in the local or the redis cache.
     *                         this also makes it possible to load from Sql etc.
     * @param saveProvider     the class that is used to save the cache entries.
     * @param localCacheExpire the time in Minutes when the local Cache Expires. (default: 21 Min)
     * @param redisCacheExpire the time in Seconds then the Redis Cache Should expire (default: 3600 sec.(1h))
     */
    public Cache(String name, int localCacheExpire, long redisCacheExpire, Function<K, V> function, AbstractSaveProvider<K, V> saveProvider) {
        System.out.println("creating cache " + name + " " + localCacheExpire + " " +redisCacheExpire);
        this.name = name;
        this.function = function;
        this.updateEvents = new ArrayList<>();

        if (ServiceCluster.isDebug()) {
            provider = new MockProvider<>(this);
        } else {
            provider = new RedisProvider<>(this, redisCacheExpire);
        }

        localCache = CacheBuilder.newBuilder()
                .expireAfterWrite(localCacheExpire, TimeUnit.MINUTES)
                .build(new CacheLoader<K, Optional<V>>() {
                    public Optional<V> load(K key) {
                        V result = provider.get(key);

                        if (result == null) {
                            result = function.apply(key);
                            if (result != null) {
                                update(key, result);
                            }
                        }

                        return Optional.ofNullable(result);
                    }
                });

        if (saveProvider != null) {
            this.saveProvider = saveProvider;

            this.saveProvider.load().forEach(this::update); //todo implement not loading every time services get started

            ScheduledTaskProvider.getInstance().register(saveProvider);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> saveAll(saveProvider.getSavableEntries())));
        }
    }

    /**
     * calls the "saveAll" implementation from the AbstractSaveProvider and removes the values that where updated successfully.
     */
    private void saveAll(HashMap<K, V> values) {
        saveProvider.saveAll(values);
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
     * This method will try to apply the value from local cache, if it is not there it will try and apply it from
     * the redis cache, if it still is nowhere to be found it will execute the function defined on the constructor of this class
     *
     * @return the value that was found in the cache or generated from the function, null if that function returns null.
     */
    private V getWithFunction(@NonNull K key, @NonNull boolean force, Function<K, V> function) {
        if (force) {
            V result = this.function.apply(key);

            if (result == null) {
                return null;
            }

            this.update(key, result);
            return result;
        }

        if (function != null) {
            try {
                return localCache.get(key, () -> {
                    V result = provider.get(key);

                    if (result == null) {
                        result = function.apply(key);
                        if (result != null) {
                            update(key, result);
                        }
                    }

                    return Optional.ofNullable(result);
                }).orElse(null);
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            Optional<V> v = localCache.get(key);
            return v.orElse(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * removes a item from the cache.
     *
     * @param key the Key of the Item that should be removed
     */
    public void removeFromCache(K key) {
        localCache.invalidate(key);
        provider.remove(key);
    }

    /**
     * updates a value to local cache and if a saveProvider is given will put it in the Queue to be saved later via the save Method.
     */
    public void update(@NonNull K key, V value) {
        if (value == null) {
            return;
        }

        CacheUpdateEvent<K, V> event = new CacheUpdateEvent(key, value);
        for (Consumer<CacheUpdateEvent> updateEvent : updateEvents) {
            updateEvent.accept(event);
        }

        if (event.isCancelled()) {
            return;
        }

        localCache.put(event.getKey(), Optional.of(event.getValue()));

        provider.update(key, value);

        if (saveProvider != null) {
            if (saveAfterGet) {
                saveProvider.getSavableEntries().put(key, value);
            }
        }
    }

    /**
     * Adds a a Consumer with a Event that will be executed before something is updated.
     *
     * @param updateEvent
     */
    public void registerUpdateEvent(Consumer<CacheUpdateEvent> updateEvent) {
        this.updateEvents.add(updateEvent);
    }

    /**
     * @return all values from the provider cache as a List of Values
     */
    public List<V> getExistingValues() {
        return new ArrayList<>(provider.getPresentValues().values());
    }

    /**
     * @return all values from the provider cache as a HashMap
     */
    public HashMap<K, V> getExistingValuesAsMap() {
        return provider.getPresentValues();
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

    public void loadCache() {
        if (saveProvider != null) {
            saveProvider.load();
        }
    }

    /**
     * clears local and redis cache.
     */
    public void clearCache() {
        provider.clear();
        clearLocalCache();
    }

    /**
     * clears the local guava cache.
     */
    public void clearLocalCache() {
        localCache.invalidateAll();
    }
}
