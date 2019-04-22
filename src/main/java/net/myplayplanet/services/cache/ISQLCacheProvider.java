package net.myplayplanet.services.cache;

import lombok.Getter;
import net.myplayplanet.services.schedule.IScheduledTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class ISQLCacheProvider<T extends Serializable> implements IScheduledTask {

    private List<String> cacheNames;
    private TimeUnit intervalUnit;
    private int interval;

    public ISQLCacheProvider(TimeUnit intervalUnit, int interval, String... cacheName) {
        this.cacheNames = new ArrayList<>();
        this.cacheNames.addAll(Arrays.asList(cacheName));
        this.intervalUnit = intervalUnit;
        this.interval = interval;
    }

    public void addCache(String cacheName) {
        cacheNames.add(cacheName);
    }

    public void removeCache(String cacheName) {
        cacheNames.remove(cacheName);
    }

    public List<Cache<T>> getCaches() {
        List<Cache<T>> caches = new ArrayList<>();

        for (String cacheName : cacheNames) {
            caches.add(CachingProvider.getInstance().getCache(cacheName));
        }
        return caches;
    }

    public Cache<T> getCache(String name) {
        return CachingProvider.getInstance().getCache(name);
    }

    public abstract void update(Cache<T> cache);

    public void runLater() {
        for (Cache<T> cache : getCaches()) {
            update(cache);
        }
    }
}
