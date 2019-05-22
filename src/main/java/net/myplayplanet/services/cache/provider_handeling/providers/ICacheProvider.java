package net.myplayplanet.services.cache.provider_handeling.providers;

import net.myplayplanet.services.cache.Cache;

import java.io.Serializable;
import java.util.HashMap;

public interface ICacheProvider<K extends Serializable, V extends Serializable> {
    Cache<K, V> getCache();
    V get(K key);
    void update(K key, V value);
    HashMap<K, V> getPresentValues();
    long expireAfterSeconds();
    void clear();
    void remove(K key);
}
