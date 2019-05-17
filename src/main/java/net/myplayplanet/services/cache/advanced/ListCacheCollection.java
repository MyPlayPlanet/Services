package net.myplayplanet.services.cache.advanced;

import net.myplayplanet.services.cache.AbstractSaveProvider;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class ListCacheCollection<A extends Serializable, B extends Serializable, V extends Serializable> {
    private HashMap<A, ListCache<B, V>> mapCacheCollection;
    private String collectionName;

    private BiFunction<A, B, V> singleEntryFunction;
    private BiFunction<A, V, B> getKeyFromValue;
    private CacheCollectionSaveProvider<A, B, V> saveProvider;

    public ListCacheCollection(String collectionName,
                               BiFunction<A, B, V> singleEntryFunction,
                               BiFunction<A, V, B> getKeyFromValue,
                               CacheCollectionSaveProvider<A, B, V> saveProvider) {
        this.collectionName = collectionName;
        this.singleEntryFunction = singleEntryFunction;
        this.getKeyFromValue = getKeyFromValue;
        this.saveProvider = saveProvider;
        this.mapCacheCollection = new HashMap<>();
    }

    public ListCache<B, V> getCache(A masterKey) {
        if (!mapCacheCollection.containsKey(masterKey)) {
            mapCacheCollection.put(masterKey, new ListCache<>(masterKey.toString() + "_" + collectionName,
                            k -> singleEntryFunction.apply(masterKey, k),
                            new AbstractSaveProvider<B, V>() {
                                @Override
                                public boolean save(B key, V value) {
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
                                public HashMap<B, V> load() {
                                    return saveProvider.load(masterKey);
                                }

                                @Override
                                public List<B> saveAll(HashMap<B, V> values) {
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
