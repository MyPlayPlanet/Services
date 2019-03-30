package net.myplayplanet.services.cache;

import java.io.Serializable;

public interface ISQLCacheProvider<T extends Serializable> {
    void update(Cache<T> object);
    void updateLater(Cache<T> object);
}
