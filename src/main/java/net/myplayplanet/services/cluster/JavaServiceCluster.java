package net.myplayplanet.services.cluster;

import net.myplayplanet.services.config.provider.IResourceProvider;
import net.myplayplanet.services.internal.exception.BadSetupException;

public class JavaServiceCluster extends BaseServiceCluster {
    protected JavaServiceCluster(IResourceProvider resourceProvider, boolean setupScheduler, boolean setupConfig, boolean setupConn) throws BadSetupException {
        super(resourceProvider, setupScheduler, setupConfig, setupConn);
    }
}
