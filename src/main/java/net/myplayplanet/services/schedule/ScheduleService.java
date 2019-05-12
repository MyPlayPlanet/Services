package net.myplayplanet.services.schedule;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.logger.Log;

@Slf4j
public class ScheduleService extends AbstractService {

    @Override
    public void init() {
        Log.getLog(log).info("Starting {service}...", "ScheduleService");
        new ScheduledTaskProvider();
    }

    @Override
    public void disable() {
        Log.getLog(log).info("Shutting down {service}...", "ScheduleService");
        ScheduledTaskProvider.getInstance().getStartedTask().values().forEach(future -> {
            future.cancel(false);
        });
    }


}
