package net.myplayplanet.services.cache;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.logger.Log;

@Slf4j
public class CachingService extends AbstractService {

    @Override
    public void init() {
        Log.getLog(log).info("Starting {service}...", "CachingService");
        new CachingProvider();
    }

    @Override
    public void disable() {
        Log.getLog(log).info("Shutting down {service}...", "CachingService");
    }

}
