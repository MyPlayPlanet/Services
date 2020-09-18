package net.myplayplanet.services.api;

import net.myplayplanet.services.config.api.IResourceProvider;
import net.myplayplanet.services.internal.exception.BadSetupException;

import java.util.Properties;

public interface IClusterBuilder {
    IClusterBuilder withDefault();

    IClusterBuilder mock(Properties properties);

    IClusterBuilder mock(boolean debug, String configPath);

    IClusterBuilder withConfig();

    IClusterBuilder withConn();

    IClusterBuilder withScheduler();

    IServiceCluster build(IResourceProvider resourceProvider) throws BadSetupException;

    IServiceCluster build() throws BadSetupException;

    IServiceCluster buildDefault(IResourceProvider resourceProvider) throws BadSetupException;

    IServiceCluster buildDefault() throws BadSetupException;
}
