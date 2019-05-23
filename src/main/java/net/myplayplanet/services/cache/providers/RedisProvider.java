package net.myplayplanet.services.cache.providers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.cache.CacheObject;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.logger.Log;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Slf4j
public class RedisProvider<K extends Serializable, V extends Serializable> implements ICacheProvider<K, V> {
    @Getter
    private Cache<K, V> cache;
    private long expireAfterSeconds;


    public RedisProvider(Cache<K, V> cache) {
        this(cache, 3600);
    }

    public RedisProvider(Cache<K, V> cache, long expireAfterSeconds) {
        this.cache = cache;
        this.expireAfterSeconds = expireAfterSeconds;
    }

    @Override
    public V get(K key) {
        try {
            byte[] keyAsByteArray = SerializationUtils.serialize(key);
            byte[] objectData = ConnectionManager.getInstance().getByteConnection().async().hget(this.getCache().getName().getBytes(), keyAsByteArray).get();

            if (objectData == null) {
                return null;
            }

            CacheObject<V> value = SerializationUtils.deserialize(objectData);

            //this makes is so that if the cache entry is older that one Hour it will be removed from redis and the cache is forced to reload it.

            if (new Timestamp(System.currentTimeMillis()).after(new Timestamp(value.getRefreshOn()))) {
                ConnectionManager.getInstance().getByteConnection().async().hdel(this.getCache().getName().getBytes(), keyAsByteArray);
                return null;
            }

            return value.getValue();
        } catch (InterruptedException | ExecutionException e) {
            Log.getLog(log).error(e, "Error while getting {key} from cache {name}.", key.toString(), this.getCache().getName());
            return null;
        }
    }

    @Override
    public void update(K key, V value) {
        CacheObject<V> v = new CacheObject<>(System.currentTimeMillis() + this.expireAfterSeconds()*1000, value);
        ConnectionManager.getInstance().getByteConnection().async().hset(this.getCache().getName().getBytes(), SerializationUtils.serialize(key), SerializationUtils.serialize(v));
    }

    @Override
    public HashMap<K, V> getPresentValues() {
        HashMap<K, V> map = new HashMap<>();

        try {
            ConnectionManager.getInstance().getByteConnection().async()
                    .hgetall(this.getCache().getName().getBytes()).get()
                    .forEach((key, value) ->
                            {
                                CacheObject<V> deserialize = SerializationUtils.deserialize(value);
                                map.put(SerializationUtils.deserialize(key), deserialize.getValue());
                            }
                    );
        } catch (InterruptedException | ExecutionException e) {
            Log.getLog(log).error(e,"Error while reloading Cache {cache}", this.getCache().getName());
        }
        return map;
    }

    @Override
    public long expireAfterSeconds() {
        return expireAfterSeconds;
    }

    @Override
    public void clear() {
        ConnectionManager.getInstance().getByteConnection().async().del(this.getCache().getName().getBytes());
    }

    @Override
    public void remove(K key) {
        ConnectionManager.getInstance().getByteConnection().async().hdel(this.getCache().getName().getBytes(), SerializationUtils.serialize(key));
    }
}
