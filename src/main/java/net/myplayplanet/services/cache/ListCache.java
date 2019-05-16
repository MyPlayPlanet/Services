package net.myplayplanet.services.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class ListCache<K extends Serializable, V extends Serializable> extends Cache<K, V>{

    private Cache<String, K> keyCache;

    public ListCache(String name, Function<K, V> function, ) {
        super(name, function);

        keyCache = new Cache<>("key_" + name, s -> null, new AbstractSaveProvider<String, K>() {
            @Override
            public boolean save(String key, K value) {
                return false;
            }

            @Override
            public HashMap<String, K> load() {
                return super.load();
            }
        });
    }

    public ListCache(String name, Function<K, V> function, AbstractSaveProvider<K, V> saveProvider) {
        super(name, function, saveProvider);
        keyCache = new Cache<>("key_" +name, s -> null);
    }

    public List<V> getList() {

    }
}
