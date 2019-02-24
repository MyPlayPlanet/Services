package net.myplayplanet.services.connection;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.logger.Log;

@Slf4j
public class ConnectionService extends AbstractService {
    @Override
    public void init() {
        new ConnectionManager(ServiceCluster.get(ConfigService.class).getRedisSettings(), ServiceCluster.get(ConfigService.class).getMySQLSettings());
    }

    @Override
    public void disable() {
        Log.getLog(log).info("shutting down {service}...", "ConnectionService");
    }
}