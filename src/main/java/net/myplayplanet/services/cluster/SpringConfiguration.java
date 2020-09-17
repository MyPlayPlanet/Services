package net.myplayplanet.services.cluster;

import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.provider.IConfigManager;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.internal.exception.BadSetupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {
    @Bean
    @Autowired
    public SpringServiceCluster springServiceCluster(ClusterBuilder clusterBuilder, ApplicationArguments arguments) throws BadSetupException {
        SpringServiceCluster springServiceCluster = clusterBuilder.buildSpring();
        springServiceCluster.startup(arguments.getSourceArgs());
        return springServiceCluster;
    }

    @Bean
    @Autowired
    public IConfigManager iConfigManager(SpringServiceCluster springServiceCluster) {
        return springServiceCluster.get(ConfigService.class).getConfigManager();
    }

    @Bean
    @Autowired
    public ConnectionManager connectionManager(SpringServiceCluster springServiceCluster) {
        return springServiceCluster.get(ConnectionService.class).getConnectionManager();
    }
}
