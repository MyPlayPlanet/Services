package net.myplayplanet.services.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.provider.AbstractConfigProvider;
import net.myplayplanet.services.config.provider.FileProvider;
import net.myplayplanet.services.config.provider.MockProvider;
import net.myplayplanet.services.connection.ConnectionSettings;
import net.myplayplanet.services.logger.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ConfigManager {
    @Getter
    private static ConfigManager instance = null;
    private AbstractConfigProvider provider;

    public static ConfigManager createInstance(File path) {
        if (instance == null) {
            instance = new ConfigManager(path);
        }
        return instance;
    }

    //todo make this with refleciton.
    private ConfigManager(File path) {
        instance = this;

        if (ServiceCluster.isDebug()) {
            provider = new MockProvider(path);
            Log.getLog(log).debug("creating Config Manager on File {path}", path.getPath());
        } else {
            provider = new FileProvider(path);
            Log.getLog(log).debug("creating Config Manager on File {path}", path.getPath());
        }
    }

    public boolean createSettingWithProperties(String name, Properties properties) throws IOException {
        return provider.createSettingWithProperties(name, properties);
    }

    public void createSettingWithProperties(File file, Properties properties) throws IOException {
        provider.createSettingWithProperties(file, properties);
    }

    public <T> T getProperty(String settingsName, String key) {
        return provider.getProperty(settingsName, key);
    }

    public <T> T getProperty(File file, String key) {
        return provider.getProperty(file, key);
    }

    public ConnectionSettings getConnectionSettings(String name) {
        return provider.getConnectionSettings(name);
    }

    public ConnectionSettings getConnectionSettings(File file) {
        return provider.getConnectionSettings(file);
    }

    public HashMap<String, ConnectionSettings> getAllSettingsFromDirectory(File file){
        return provider.getAllSettingsFromDirectory(file);
    }
}