package net.myplayplanet.services.schedule.api;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public interface IScheduledTaskProvider {
    <T extends IScheduledTask> void register(T task);

    <T extends IScheduledTask> ScheduledFuture get(T task);

    List<IScheduledTask> getUpdatebleObjects();

    HashMap<String, ScheduledFuture> getStartedTask();

    ScheduledExecutorService getExecutorService();
}
