package net.myplayplanet.services.rest;

import lombok.Getter;
import net.myplayplanet.services.config.api.IConfigManager;

import java.io.IOException;
import java.util.Properties;

public class AbstractRestManager {

    private final IConfigManager configManager;
    @Getter
    private final String baseUrl;

    public AbstractRestManager(IConfigManager configManager) throws IOException {
        this(configManager, null);
    }

    public AbstractRestManager(IConfigManager configManager, String instanceName) throws IOException {
        this.configManager = configManager;
        Properties properties = new Properties();
        properties.setProperty("baseUrl", "http://localhost:8080/");

        String settingsName = "rest-setting";
        if (instanceName != null) {
            settingsName = String.format("rest-%s-setting", instanceName);
        }
        this.configManager.createSettingWithProperties(settingsName, properties);
        this.baseUrl = this.configManager.getProperty(settingsName, "baseUrl");
    }
}
