package net.myplayplanet.services.config;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.provider.AbstractConfigProvider;
import net.myplayplanet.services.config.provider.FileProvider;
import net.myplayplanet.services.config.provider.MockProvider;
import net.myplayplanet.services.connection.ConnectionSettings;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

@Slf4j
public class ConfigManager {
    private AbstractConfigProvider provider;

    public ConfigManager(File path, boolean debug) {
        if (debug) {
            System.out.println("creating Config Manager on File " + path.getPath());
            provider = new MockProvider(path);
        } else {
            System.out.println("creating Mock Config Manager on File " + path.getPath());
            provider = new FileProvider(path);
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

    public HashMap<String, ConnectionSettings> getAllSettingsFromDirectory(File file) {
        return provider.getAllSettingsFromDirectory(file);
    }
}