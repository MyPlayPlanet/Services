package net.myplayplanet.services.logger;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.logger.sinks.MySQLSink;

@Slf4j
public class LoggerService extends AbstractService {
    @Override
    public void init() {
        Log.initialize(new MySQLSink());
    }

    @Override
    public void disable() {
        Log.getLog(log).info("Shutting down {service}...", "LoggerService");
    }
}