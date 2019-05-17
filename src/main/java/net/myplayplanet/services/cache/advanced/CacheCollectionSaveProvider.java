package net.myplayplanet.services.cache.advanced;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface CacheCollectionSaveProvider<MK extends Serializable, K extends Serializable, V extends Serializable> {
    boolean save(MK masterKey,K key, V value);

    default TimeUnit getIntervalUnit() {
        return TimeUnit.SECONDS;
    }
    default int getInterval() {
        return 60;
    }

    HashMap<K, V> load(MK masterKey);

    List<K> saveAll(MK masterKey,HashMap<K, V> values);
}
