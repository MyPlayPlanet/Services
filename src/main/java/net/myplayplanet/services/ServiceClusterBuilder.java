package net.myplayplanet.services;

import net.myplayplanet.services.config.provider.IResourceProvider;
import net.myplayplanet.services.internal.exception.BadSetupException;

import java.util.Properties;

public class ServiceClusterBuilder {
    private boolean setupScheduler = false;
    private boolean setupConfig = false;
    private boolean setupConn = false;
    private Properties mock = null;

    public ServiceClusterBuilder() {}

    public ServiceClusterBuilder withDefault() {
        setupScheduler = true;
        setupConfig = true;
        setupConn = true;
        return this;
    }

    public ServiceClusterBuilder mock(Properties properties) {
        this.mock = properties;
        return this;
    }
    public ServiceClusterBuilder mock(boolean debug, String configPath) {
        Properties properties = new Properties();
        properties.put("mpp.basic.debug", ""+debug);
        properties.put("mpp.basic.config-path", configPath);
        this.mock = properties;
        return this;
    }

    public ServiceClusterBuilder withConfig() {
        this.setupConfig = true;
        return this;
    }

    public ServiceClusterBuilder withConn() {
        this.setupConfig = true;
        this.setupConn = true;
        return this;
    }

    public ServiceClusterBuilder withScheduler() {
        this.setupScheduler = true;
        return this;
    }

    public ServiceCluster build(IResourceProvider resourceProvider) throws BadSetupException {
        return new ServiceCluster(resourceProvider, setupScheduler, setupConfig, setupConn);
    }

    public ServiceCluster build() throws BadSetupException {
        return this.build(IResourceProvider.getResourceProvider(this.mock));
    }

    public ServiceCluster buildDefault(IResourceProvider resourceProvider) throws BadSetupException {
        this.withDefault();
        return this.build(resourceProvider);
    }

    public ServiceCluster buildDefault() throws BadSetupException {
        return this.buildDefault(IResourceProvider.getResourceProvider(this.mock));
    }
}
