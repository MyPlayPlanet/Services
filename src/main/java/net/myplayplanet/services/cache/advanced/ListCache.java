package net.myplayplanet.services.cache.advanced;

import net.myplayplanet.services.cache.AbstractSaveProvider;

import java.io.Serializable;
import java.util.function.Function;

public class ListCache<K extends Serializable, V extends Serializable> extends MapCache<K, V> {

    private Function<V, K> getKeyFromValue;

    public ListCache(String name, Function<K, V> getSingleValue, AbstractSaveProvider<K, V> saveProvider, Function<V, K> getKeyFromValue) {
        super(name, getSingleValue, saveProvider);
        this.getKeyFromValue = getKeyFromValue;
    }

    public void addItem(V item) {
        super.addItem(getKeyFromValue.apply(item), item);
    }


}


