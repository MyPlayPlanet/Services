package net.myplayplanet.services.cache.providers;

import lombok.Getter;
import lombok.Setter;
import net.myplayplanet.services.cache.Cache;

import java.io.Serializable;
import java.util.HashMap;

public class MockProvider<K extends Serializable, V extends Serializable> implements ICacheProvider<K, V> {

    @Getter
    @Setter
    private static int expireAfterSeconds = 10;
    @Getter
    private static HashMap<String, HashMap> totalMap = new HashMap<>();

    private HashMap<K, V> map;


    private Cache<K, V> cache;

    public MockProvider(Cache<K, V> cache) {
        this.cache = cache;
        map = new HashMap<>();
        totalMap.put(cache.getName(), map);
    }

    @Override
    public Cache<K, V> getCache() {
        return null;
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public void update(K key, V value) {
        map.put(key, value);
    }

    @Override
    public HashMap<K, V> getPresentValues() {
        return map;
    }

    @Override
    public long expireAfterSeconds() {
        return getExpireAfterSeconds();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public void remove(K key) {
        map.remove(key);
    }
}
