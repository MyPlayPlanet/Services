package net.myplayplanet.services.cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.logger.Log;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Slf4j
public class Cache<T extends Serializable> {

    private String cacheName;
    @Getter(AccessLevel.PROTECTED)
    private HashMap<UUID, CacheObject<T>> cachedObjects;
    @Setter
    @Getter
    private ISQLCacheProvider provider;

    public Cache(String name) {
        this.cacheName = name;
        this.cachedObjects = new HashMap<>();
        this.provider = null;
    }

    public Collection<CacheObject<T>> getCacheObjects(){
        this.updateLocal();
        return cachedObjects.values();
    }

    public CacheObject add(T object) {
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
    public T getObject(UUID cacheObjectID) throws ConcurrentModificationException {
        return SerializationUtils.deserialize(this.getCacheObject(cacheObjectID).getData());
    }

    public List<T> getObjects() {
        List<T> output = new ArrayList<>();
        for (CacheObject<T> cacheObject : getCacheObjects()) {
            output.add(cacheObject.toType());
        }
        return output;
    }

    public HashMap<UUID, T> getObjectMap() {
        HashMap<UUID, T> output = new HashMap<>();
        for (CacheObject<T> cacheObject : getCacheObjects()) {
            output.put(cacheObject.getCachedObjectID(), cacheObject.toType());
        }
        return output;
    }

    private static <K, V> Stream<K> keys(Map<K, V> map, V value) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey);
    }

    public UUID getCacheUuidFromObject(T t) {
        return keys(getObjectMap(), t).findFirst().orElse(null);
    }

    public CacheObject<T> getCacheObject(UUID objectID) {
        this.updateLocal();
        return this.cachedObjects.get(objectID);
    }

    private void updateLocal() {
        this.cachedObjects.clear();
        HashMap<UUID, CacheObject<T>> cacheMap = new HashMap<>();

        try {
            Map<byte[], byte[]> byteCache = ConnectionManager.getInstance().getByteConnection().async().hgetall(this.cacheName.getBytes()).get();
            byteCache.forEach((id, object) -> {
                UUID uuid = SerializationUtils.deserialize(id);
                CacheObject<T> cacheObject = SerializationUtils.deserialize(object);
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

    private void updateMySql() {
        if (getProvider() == null) {
            return;
        }
        getProvider().update(this);
    }

    public void cleanup(){
        for (CacheObject cacheObject : this.getCacheObjects()) {
            this.removeRemote(cacheObject);
        }
        this.updateLocal();
    }
}
