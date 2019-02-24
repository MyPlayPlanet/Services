package net.myplayplanet.services.logger;

import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.logger.sinks.MySQLSink;

public class LoggerService extends AbstractService {
    @Override
    public void init() {
        Log.initialize(new MySQLSink());
    }
}
