package net.myplayplanet.services.rest;

import lombok.Getter;
import net.myplayplanet.services.config.provider.IConfigManager;

import java.io.IOException;
import java.util.Properties;

public class AbstractRestManager {

    private IConfigManager configManager;
    @Getter
    private String baseUrl;

    public AbstractRestManager(IConfigManager configManager) throws IOException {
        this.configManager = configManager;
        Properties properties = new Properties();
        properties.setProperty("baseUrl", "http://localhost:8080/");
        this.configManager.createSettingWithProperties("rest-setting", properties);
        this.baseUrl = this.configManager.getProperty("rest-setting", "baseUrl");
    }

}
