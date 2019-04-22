package net.myplayplanet.services.checker;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.logger.Log;

@Slf4j
public class CheckService extends AbstractService {

    @Override
    public void init() {
        Log.getLog(log).info("Starting {service}...", "ScheduleService");
        new CheckStringManager();
    }

    @Override
    public void disable() {
        Log.getLog(log).info("Shutting down {service}...", "ScheduleService");
    }
}
