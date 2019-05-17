package net.myplayplanet.services.cache.provider_handeling;

import lombok.Getter;
import net.myplayplanet.services.cache.Cache;

import java.io.Serializable;
import java.util.HashMap;

public abstract class AbstractCacheProvider<K extends Serializable, V extends Serializable> {
    @Getter
    private Cache<K, V> cache;

    public AbstractCacheProvider(Cache<K, V> cache) {
        this.cache = cache;
    }

    public abstract V get(K key);
    public abstract void update(K key, V value);

    public abstract HashMap<K, V> getPresentValues();

    public abstract long expireAfterSeconds();

    public abstract void clear();

    public abstract void remove(K key);
}
