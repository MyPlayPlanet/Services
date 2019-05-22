package net.myplayplanet.services.cache.provider_handeling.providers;

import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.cache.provider_handeling.AbstractCacheProvider;
import net.myplayplanet.services.cache.provider_handeling.DebugProvider;

import java.io.Serializable;
import java.util.HashMap;

public class MockProvider<K extends Serializable, V extends Serializable> extends AbstractCacheProvider<K, V> implements DebugProvider {

    private AbstractCacheProvider<K, V> provider;
    private HashMap<K, V> map;

    public MockProvider(Cache<K, V> cache) {
        super(cache);
    }


    public MockProvider(Cache<K, V> cache, AbstractCacheProvider<K, V> provider) {
        super(cache);
        if (provider != null) {
            this.provider = provider;
        }else {
            map = new HashMap<>();
        }
    }

    @Override
    public V get(K key) {
        if (provider != null) {
            return provider.get(key);
        }
        return null;
    }

    @Override
    public void update(K key, V value) {
        if (provider != null) {
            provider.update(key, value);
            return;
        }


    }

    @Override
    public HashMap<K, V> getPresentValues() {
        return provider.getPresentValues();
    }

    @Override
    public long expireAfterSeconds() {
        return provider.expireAfterSeconds();
    }

    @Override
    public void clear() {
        provider.clear();
    }

    @Override
    public void remove(K key) {

    }
}
