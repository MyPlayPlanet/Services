package net.myplayplanet.services.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.provider.IConfigManager;

import java.io.File;

@Getter
@Setter
@Slf4j
public class ConfigService extends AbstractService {

    @Setter(AccessLevel.PROTECTED)
    protected IConfigManager configManager;
    @NonNull
    private File path;

    public ConfigService(ServiceCluster cluster, File configPath, IConfigManager configManager) {
        super(cluster);
        this.path = configPath;
        this.configManager = configManager;
    }

    @Override
    public void init() {
        System.out.println("Started ConfigService");
    }
}
