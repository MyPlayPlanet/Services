package net.myplayplanet.services.cluster;

import lombok.Getter;
import net.myplayplanet.services.api.IClusterBuilder;

import java.util.Properties;

@Getter
public abstract class BaseClusterBuilder implements IClusterBuilder {
    private boolean setupScheduler = false;
    private boolean setupConfig = false;
    private boolean setupConn = false;
    private Properties mock = null;

    public BaseClusterBuilder() {
    }

    @Override
    public IClusterBuilder withDefault() {
        setupScheduler = true;
        setupConfig = true;
        setupConn = true;
        return this;
    }

    @Override
    public IClusterBuilder mock(Properties properties) {
        this.mock = properties;
        return this;
    }

    @Override
    public IClusterBuilder mock(boolean debug, String configPath) {
        Properties properties = new Properties();
        properties.put("mpp.basic.debug", "" + debug);
        properties.put("mpp.basic.config-path", configPath);
        this.mock = properties;
        return this;
    }

    @Override
    public IClusterBuilder withConfig() {
        this.setupConfig = true;
        return this;
    }

    @Override
    public IClusterBuilder withConn() {
        this.setupConfig = true;
        this.setupConn = true;
        return this;
    }

    @Override
    public IClusterBuilder withScheduler() {
        this.setupScheduler = true;
        return this;
    }
}
