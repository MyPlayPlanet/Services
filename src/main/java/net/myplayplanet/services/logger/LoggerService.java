package net.myplayplanet.services.logger;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.logger.sinks.ISink;
import net.myplayplanet.services.logger.sinks.MockSink;
import net.myplayplanet.services.logger.sinks.MySQLSink;

@Slf4j
public class LoggerService extends AbstractService {
    @Getter
    @Setter
    private static ISink defaultSink = null;
    @Override
    public void init() {
        if (ServiceCluster.isDebug()) {
            defaultSink = new MockSink();
        }else if (defaultSink == null) {
            defaultSink = new MySQLSink();
        }
        Log.initialize(defaultSink);
    }

    @Override
    public void disable() {
        Log.getLog(log).info("Shutting down {service}...", "LoggerService");
    }
}