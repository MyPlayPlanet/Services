package net.myplayplanet.services.logger;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.logger.sinks.ISink;
import net.myplayplanet.services.logger.sinks.MockSink;
import net.myplayplanet.services.logger.sinks.MySQLSink;

@Slf4j
public class LoggerService extends AbstractService {
    @Override
    public void init() {
        ISink iSink = (ServiceCluster.isDebug()) ? new MockSink() : new MySQLSink();
        Log.initialize(iSink);
    }

    @Override
    public void disable() {
        Log.getLog(log).info("Shutting down {service}...", "LoggerService");
    }
}