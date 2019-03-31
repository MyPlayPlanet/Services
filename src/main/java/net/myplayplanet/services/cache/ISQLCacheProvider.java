package net.myplayplanet.services.cache;

import lombok.Getter;
import net.myplayplanet.services.schedule.IScheduledTask;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class ISQLCacheProvider<T extends Serializable> implements IScheduledTask {

    private String cacheName;
    private TimeUnit intervalUnit;
    private int interval;

    public ISQLCacheProvider(String cacheName, TimeUnit intervalUnit, int interval) {
        this.cacheName = cacheName;
        this.intervalUnit = intervalUnit;
        this.interval = interval;
    }

    public Cache<T> getCache() {
        return CachingProvider.getInstance().getCache(cacheName);
    }

    abstract void update();

    public void runLater() {
        update();
    }
}
