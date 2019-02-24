package net.myplayplanet.services.connection;

import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigService;

public class ConnectionService extends AbstractService {
    @Override
    public void init() {
        new ConnectionManager(ServiceCluster.get(ConfigService.class).getRedisSettings(), ServiceCluster.get(ConfigService.class).getMySQLSettings());
    }
}
