package net.myplayplanet.services.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.IService;
import net.myplayplanet.services.config.provider.IConfigManager;
import net.myplayplanet.services.config.provider.IResourceProvider;
import net.myplayplanet.services.config.provider.config.FileConfigManager;
import net.myplayplanet.services.config.provider.config.MockConfigManager;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.connection.dbversion.UpdateCommand;
import net.myplayplanet.services.internal.CommandExecutor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
@Slf4j
public class ConfigService implements IService {
    private IConfigManager configManager;
    private final IResourceProvider resourceProvider;
    private File path;
    private boolean debug;

    public ConfigService(IResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void init() {
        Properties properties = new Properties();
        try (InputStream inputStream = this.resourceProvider.getResourceFile("service.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String configPath = properties.getProperty("mpp.basic.config-path");
        File configFile = new File(configPath);

        this.debug = Boolean.parseBoolean(properties.getProperty("mpp.basic.debug"));
        this.configManager = debug
                ? new MockConfigManager(configFile)
                : new FileConfigManager(configFile);

        this.path = configManager.getPath();
        System.out.println("Started ConfigService");
    }

}
