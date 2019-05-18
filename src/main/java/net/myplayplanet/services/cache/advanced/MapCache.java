package net.myplayplanet.services.cache.advanced;

import net.myplayplanet.services.cache.AbstractSaveProvider;
import net.myplayplanet.services.cache.Cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

public class MapCache<K extends Serializable, V extends Serializable> {

    private Cache<K, V> saveCache;
    private Cache<Integer, HashMap<K, V>> listCache;

    public MapCache(String name, Function<K, V> singleEntryGetter, AbstractSaveProvider<K, V> singleEntrySaveProvider) {
        saveCache = new Cache<>(name, singleEntryGetter, singleEntrySaveProvider);
        listCache = new Cache<>("list_" + name, integer -> {
            HashMap<K, V> map = saveCache.getExistingValuesAsMap();
            singleEntrySaveProvider.load().forEach(map::put);
            return map;
        });
    }

    public Collection<V> getList() {
        return listCache.get(0).values();
    }

    public HashMap<K, V> getMap() {
        return listCache.get(0);
    }

    public void addItem(K key, V value) {
        saveCache.update(key, value);

        HashMap<K, V> v = listCache.get(0);
        v.put(key, value);
        listCache.update(0, v);
    }

    public void removeItem(K key) {
        HashMap<K, V> v = listCache.get(0);
        v.remove(key);
        listCache.update(0, v);

        saveCache.removeFromCache(key);
    }

    public void clear() {
        saveCache.clearCache();
        listCache.clearCache();
    }

    public V get(K key) {
        V v = saveCache.get(key);

        HashMap<K, V> kvHashMap = listCache.get(0);
        if (!kvHashMap.containsKey(key)) {
            listCache.update(0, kvHashMap);
        }

        return v;
    }

}
