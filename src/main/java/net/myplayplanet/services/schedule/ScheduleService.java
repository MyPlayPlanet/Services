package net.myplayplanet.services.schedule;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.api.IService;

@Slf4j
public class ScheduleService implements IService {

    public ScheduleService() {
    }

    @Override
    public void init() {
        System.out.println("Starting ScheduleService...");
        new ScheduledTaskProvider();
    }

    @Override
    public void disable() {
        System.out.println("Shutting down ScheduleService...");
        ScheduledTaskProvider.getInstance().getStartedTask().values().forEach(future -> {
            future.cancel(false);
        });
    }
}
