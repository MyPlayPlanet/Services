package net.myplayplanet.services.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.logger.Log;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Getter
public class CachingProvider {

    @Getter
    private static CachingProvider instance;

    public CachingProvider() {
        instance = this;
    }

    public <T extends Serializable> Cache<T> getCache(String name) {
        HashMap<UUID, CacheObject<T>> cacheMap = new HashMap<>();

        try {
            Map<byte[], byte[]> byteCache = ConnectionManager.getInstance().getByteConnection().async().hgetall(name.getBytes()).get();
            byteCache.forEach((id, object) -> {
                cacheMap.put(SerializationUtils.deserialize(id), SerializationUtils.deserialize(object));
            });
        } catch (InterruptedException | ExecutionException e) {
            Log.getLog(log).error(e, "Error while getting Cache {cache}", name);
        }

        Cache<T> cache = new Cache(name);
        cache.getCachedObjects().putAll(cacheMap);

        return cache;
    }

    public boolean exists(String name){
        try {
            return ConnectionManager.getInstance().getByteConnection().async().exists(name.getBytes()).get() != 0;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
