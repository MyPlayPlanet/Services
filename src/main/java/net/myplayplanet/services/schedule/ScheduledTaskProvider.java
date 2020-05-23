package net.myplayplanet.services.schedule;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Getter
@Slf4j
public class ScheduledTaskProvider {

    @Getter
    private static ScheduledTaskProvider instance;
    private List<IScheduledTask> updatebleObjects;
    private HashMap<String, ScheduledFuture> startedTask;
    private ScheduledExecutorService executorService;

    public ScheduledTaskProvider() {
        instance = this;
        updatebleObjects = new ArrayList<>();
        startedTask = new HashMap<>();
        executorService = Executors.newScheduledThreadPool(2);
    }

    public <T extends IScheduledTask> void register(T task) {
        if (task.getInterval() <= 0) {
            System.out.println("ScheduledTask Interval must be higher than 0!");
            return;
        }
        this.getUpdatebleObjects().add(task);
        this.scheduleTask(task);
    }

    private <T extends IScheduledTask> void scheduleTask(T task) {
        this.startedTask.put(task.getClass().getName().toLowerCase(), this.getExecutorService().scheduleAtFixedRate(task::runLater, 0, task.getInterval(), task.getIntervalUnit()));
    }

    public <T extends IScheduledTask> ScheduledFuture get(T task) {
        return this.getStartedTask().get(task.getClass().getName().toLowerCase());
    }
}
