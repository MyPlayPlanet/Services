package net.myplayplanet.services.cluster;

import net.myplayplanet.services.config.api.IResourceProvider;
import net.myplayplanet.services.internal.exception.BadSetupException;

public class SpringClusterBuilder extends BaseClusterBuilder {

    @Override
    public SpringServiceCluster build(IResourceProvider resourceProvider) throws BadSetupException {
        return new SpringServiceCluster(resourceProvider, this.isSetupScheduler(), this.isSetupConfig(), this.isSetupConn());
    }

    @Override
    public SpringServiceCluster build() throws BadSetupException {
        return this.build(IResourceProvider.getResourceProvider(this.getMock()));
    }

    @Override
    public SpringServiceCluster buildDefault(IResourceProvider resourceProvider) throws BadSetupException {
        this.withDefault();
        return this.build(resourceProvider);
    }

    @Override
    public SpringServiceCluster buildDefault() throws BadSetupException {
        return this.buildDefault(IResourceProvider.getResourceProvider(this.getMock()));
    }
}
