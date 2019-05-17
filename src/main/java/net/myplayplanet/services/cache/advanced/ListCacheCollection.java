package net.myplayplanet.services.cache.advanced;

import net.myplayplanet.services.cache.AbstractSaveProvider;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class ListCacheCollection<MK extends Serializable, K extends Serializable, V extends Serializable> {
    private HashMap<MK, ListCache<K, V>> mapCacheCollection;
    private String collectionName;

    private BiFunction<MK, K, V> singleEntryFunction;
    private BiFunction<MK, V, K> getKeyFromValue;
    private CacheCollectionSaveProvider<MK, K, V> saveProvider;

    public ListCacheCollection(String collectionName,
                               BiFunction<MK, K, V> singleEntryFunction,
                               BiFunction<MK, V, K> getKeyFromValue,
                               CacheCollectionSaveProvider<MK, K, V> saveProvider) {
        this.collectionName = collectionName;
        this.singleEntryFunction = singleEntryFunction;
        this.getKeyFromValue = getKeyFromValue;
        this.saveProvider = saveProvider;
        this.mapCacheCollection = new HashMap<>();
    }

    public ListCache<K, V> getCache(MK masterKey) {
        if (!mapCacheCollection.containsKey(masterKey)) {
            mapCacheCollection.put(masterKey, new ListCache<>(masterKey.toString() + "_" + collectionName,
                            k -> singleEntryFunction.apply(masterKey, k),
                            new AbstractSaveProvider<K, V>() {
                                @Override
                                public boolean save(K key, V value) {
                                    return saveProvider.save(masterKey, key, value);
                                }

                                @Override
                                public TimeUnit getIntervalUnit() {
                                    return saveProvider.getIntervalUnit();
                                }

                                @Override
                                public int getInterval() {
                                    return saveProvider.getInterval();
                                }

                                @Override
                                public HashMap<K, V> load() {
                                    return saveProvider.load(masterKey);
                                }

                                @Override
                                public List<K> saveAll(HashMap<K, V> values) {
                                    return saveProvider.saveAll(masterKey, values);
                                }
                            },
                            v -> getKeyFromValue.apply(masterKey, v)
                    )
            );
        }
        return mapCacheCollection.get(masterKey);
    }


}
