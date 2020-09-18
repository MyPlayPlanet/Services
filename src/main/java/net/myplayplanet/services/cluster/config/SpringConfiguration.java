package net.myplayplanet.services.cluster.config;

import net.myplayplanet.services.cluster.SpringClusterBuilder;
import net.myplayplanet.services.cluster.SpringServiceCluster;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.api.IConfigManager;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.connection.api.IConnectionManager;
import net.myplayplanet.services.internal.exception.BadSetupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {
    @Bean
    @Autowired
    public SpringServiceCluster springServiceCluster(SpringClusterBuilder javaClusterBuilder, ApplicationArguments arguments) throws BadSetupException {
        SpringServiceCluster springServiceCluster = javaClusterBuilder.build();
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
    public IConnectionManager connectionManager(SpringServiceCluster springServiceCluster) {
        return springServiceCluster.get(ConnectionService.class).getIConnectionManager();
    }
}
