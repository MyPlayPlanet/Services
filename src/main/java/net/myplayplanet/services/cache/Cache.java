package net.myplayplanet.services.cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.logger.Log;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Cache {

    private String cacheName;
    @Getter(AccessLevel.PROTECTED)
    private HashMap<UUID, CacheObject> cachedObjects;

    public Cache(String name) {
        this.cacheName = name;
        this.cachedObjects = new HashMap<>();
    }

    public Collection<CacheObject> getCacheObjects(){
        this.updateLocal();
        return cachedObjects.values();
    }

    public CacheObject add(Serializable object) {
        UUID cacheObjectID = UUID.randomUUID();
        CacheObject cacheObject = new CacheObject(cacheObjectID, SerializationUtils.serialize(object));
        this.updateRemote(cacheObject);
        this.updateLocal();
        return cacheObject;
    }

    public CacheObject remove(UUID cacheObjectID) {
        CacheObject cacheObject = this.getCacheObject(cacheObjectID);
        this.removeRemote(cacheObject);
        this.updateLocal();
        return cacheObject;
    }

    /**
     * Don't use this in an loop
     */
    public <T> T getObject(UUID cacheObjectID) throws ConcurrentModificationException {
        return SerializationUtils.deserialize(this.getCacheObject(cacheObjectID).getData());
    }

    public CacheObject getCacheObject(UUID objectID) {
        this.updateLocal();
        return this.cachedObjects.get(objectID);
    }

    private void updateLocal() {
        this.cachedObjects.clear();
        HashMap<UUID, CacheObject> cacheMap = new HashMap<>();

        try {
            Map<byte[], byte[]> byteCache = ConnectionManager.getInstance().getByteConnection().async().hgetall(this.cacheName.getBytes()).get();
            byteCache.forEach((id, object) -> {
                UUID uuid = SerializationUtils.deserialize(id);
                CacheObject cacheObject = SerializationUtils.deserialize(object);
                System.out.println(uuid + " " + SerializationUtils.deserialize(cacheObject.getData()));
                cacheMap.put(SerializationUtils.deserialize(id), cacheObject);
            });
        } catch (InterruptedException | ExecutionException e) {
            Log.getLog(log).error(e, "Error while getting Cache {cache}", this.cacheName);
        }

        this.cachedObjects.putAll(cacheMap);
    }

    private void updateRemote(CacheObject cacheObject) {
        ConnectionManager.getInstance().getByteConnection().async().hset(this.cacheName.getBytes(), SerializationUtils.serialize(cacheObject.getCachedObjectID()), SerializationUtils.serialize(cacheObject));
    }

    private void removeRemote(CacheObject cacheObject) {
        ConnectionManager.getInstance().getByteConnection().async().hdel(this.cacheName.getBytes(), SerializationUtils.serialize(cacheObject.getCachedObjectID()));
    }

    public void cleanup(){
        for (CacheObject cacheObject : this.getCacheObjects()) {
            this.removeRemote(cacheObject);
        }
        this.updateLocal();
    }
}
