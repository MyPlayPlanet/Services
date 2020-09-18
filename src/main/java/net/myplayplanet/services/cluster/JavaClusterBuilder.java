package net.myplayplanet.services.cluster;

import net.myplayplanet.services.config.api.IResourceProvider;
import net.myplayplanet.services.internal.exception.BadSetupException;

public class JavaClusterBuilder extends BaseClusterBuilder {
    @Override
    public JavaServiceCluster build(IResourceProvider resourceProvider) throws BadSetupException {
        return new JavaServiceCluster(resourceProvider, this.isSetupScheduler(), this.isSetupConfig(), this.isSetupConn());
    }

    @Override
    public JavaServiceCluster build() throws BadSetupException {
        return this.build(IResourceProvider.getResourceProvider(this.getMock()));
    }

    @Override
    public JavaServiceCluster buildDefault(IResourceProvider resourceProvider) throws BadSetupException {
        this.withDefault();
        return this.build(resourceProvider);
    }

    @Override
    public JavaServiceCluster buildDefault() throws BadSetupException {
        return this.buildDefault(IResourceProvider.getResourceProvider(this.getMock()));
    }
}
