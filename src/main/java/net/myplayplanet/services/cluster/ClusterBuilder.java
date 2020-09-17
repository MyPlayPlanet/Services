package net.myplayplanet.services.cluster;

import net.myplayplanet.services.config.provider.IResourceProvider;
import net.myplayplanet.services.internal.exception.BadSetupException;

import java.util.Properties;

public class ClusterBuilder {
    private boolean setupScheduler = false;
    private boolean setupConfig = false;
    private boolean setupConn = false;
    private Properties mock = null;

    public ClusterBuilder() {
    }

    public ClusterBuilder withDefault() {
        setupScheduler = true;
        setupConfig = true;
        setupConn = true;
        return this;
    }

    public ClusterBuilder mock(Properties properties) {
        this.mock = properties;
        return this;
    }

    public ClusterBuilder mock(boolean debug, String configPath) {
        Properties properties = new Properties();
        properties.put("mpp.basic.debug", "" + debug);
        properties.put("mpp.basic.config-path", configPath);
        this.mock = properties;
        return this;
    }

    public ClusterBuilder withConfig() {
        this.setupConfig = true;
        return this;
    }

    public ClusterBuilder withConn() {
        this.setupConfig = true;
        this.setupConn = true;
        return this;
    }

    public ClusterBuilder withScheduler() {
        this.setupScheduler = true;
        return this;
    }

    public SpringServiceCluster buildSpring(IResourceProvider resourceProvider) throws BadSetupException {
        return new SpringServiceCluster(resourceProvider, setupScheduler, setupConfig, setupConn);
    }
    public SpringServiceCluster buildSpring() throws BadSetupException {
        return this.buildSpring(IResourceProvider.getResourceProvider(this.mock));
    }

    public SpringServiceCluster buildDefaultSpring(IResourceProvider resourceProvider) throws BadSetupException {
        this.withDefault();
        return this.buildSpring(resourceProvider);
    }

    public SpringServiceCluster buildDefaultSpring() throws BadSetupException {
        return this.buildDefaultSpring(IResourceProvider.getResourceProvider(this.mock));
    }

    public JavaServiceCluster build(IResourceProvider resourceProvider) throws BadSetupException {
        return new JavaServiceCluster(resourceProvider, setupScheduler, setupConfig, setupConn);
    }

    public JavaServiceCluster build() throws BadSetupException {
        return this.build(IResourceProvider.getResourceProvider(this.mock));
    }

    public JavaServiceCluster buildDefault(IResourceProvider resourceProvider) throws BadSetupException {
        this.withDefault();
        return this.build(resourceProvider);
    }

    public JavaServiceCluster buildDefault() throws BadSetupException {
        return this.buildDefault(IResourceProvider.getResourceProvider(this.mock));
    }
}
