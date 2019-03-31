package net.myplayplanet.services.cache;

import net.myplayplanet.services.schedule.IScheduledTask;
import net.myplayplanet.services.schedule.ScheduledTaskProvider;

import java.util.concurrent.TimeUnit;

public class exanoklCacheClearenDing implements IScheduledTask {

    public void test(){
        ScheduledTaskProvider.getInstance().register(new exanokl("adlsöfkkjajsdf", TimeUnit.MINUTES, 40));
    }

    @Override
    public TimeUnit getIntervalUnit() {
        return TimeUnit.MINUTES;
    }

    @Override
    public int getInterval() {
        return 30;
    }

    @Override
    public void runLater() {
        CachingProvider.getInstance().getCache("text-cache").cleanup();
    }
}
