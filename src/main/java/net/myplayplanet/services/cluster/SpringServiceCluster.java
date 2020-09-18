package net.myplayplanet.services.cluster;

import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.api.IConfigManager;
import net.myplayplanet.services.config.api.IResourceProvider;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.connection.api.IConnectionManager;
import net.myplayplanet.services.internal.exception.BadSetupException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.function.Supplier;

public class SpringServiceCluster extends JavaServiceCluster {
    protected SpringServiceCluster(IResourceProvider resourceProvider, boolean setupScheduler, boolean setupConfig, boolean setupConn) throws BadSetupException {
        super(resourceProvider, setupScheduler, setupConfig, setupConn);
    }

    public void startup(String[] args, Supplier<ConfigurableApplicationContext> supplier) {
        this.startup(args, () -> {
            System.out.println("initialising spring framework...");
            ConfigurableApplicationContext applicationContext = supplier.get();
            ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
            beanFactory.registerSingleton(this.getClass().getCanonicalName(), this);
            IConnectionManager IConnectionManager = this.get(ConnectionService.class).getIConnectionManager();
            IConfigManager configManager = this.get(ConfigService.class).getConfigManager();
            beanFactory.registerSingleton(IConnectionManager.getClass().getCanonicalName(), IConnectionManager);
            beanFactory.registerSingleton(configManager.getClass().getCanonicalName(), configManager);
            System.out.println("initialised springframework.");
        });
    }


    public void startup(Supplier<ConfigurableApplicationContext> supplier) {
        this.startup(null, supplier);
    }
}
