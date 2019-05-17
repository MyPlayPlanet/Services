package net.myplayplanet.services.cache.provider_handeling.providers;

import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.cache.provider_handeling.AbstractCacheProvider;
import net.myplayplanet.services.cache.provider_handeling.DebugProvider;

import java.io.Serializable;
import java.util.HashMap;

public class MockProvider<K extends Serializable, V extends Serializable> extends AbstractCacheProvider<K, V> implements DebugProvider {

    public MockProvider(Cache<K, V> cache) {
        super(cache);
    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public void update(K key, V value) {

    }

    @Override
    public HashMap<K, V> getPresentValues() {
        return null;
    }

    @Override
    public long expireAfterSeconds() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public void remove(K key) {

    }
}
