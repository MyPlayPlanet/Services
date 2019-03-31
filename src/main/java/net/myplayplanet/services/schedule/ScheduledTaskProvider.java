package net.myplayplanet.services.schedule;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.logger.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Slf4j
public class ScheduledTaskProvider {

    @Getter
    private static ScheduledTaskProvider instance;
    private List<IScheduledTask> updatebleObjects;
    private ScheduledExecutorService executorService;

    public ScheduledTaskProvider() {
        instance = this;
        updatebleObjects = new ArrayList<>();
        executorService = Executors.newScheduledThreadPool(2);
    }

    public <T extends IScheduledTask> void register(T task) {
        if (task.getInterval() <= 0) {
            Log.getLog(log).error("ScheduledTask Interval must be higher than 0!");
            return;
        }
        this.getUpdatebleObjects().add(task);
        this.scheduleTask(task);
    }

    private <T extends IScheduledTask> void scheduleTask(T task) {
        this.getExecutorService().scheduleAtFixedRate(() -> task.runLater(), 0, task.getInterval(), task.getIntervalUnit());
    }
}
