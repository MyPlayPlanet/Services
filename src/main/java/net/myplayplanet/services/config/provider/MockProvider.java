package net.myplayplanet.services.config.provider;

import net.myplayplanet.services.connection.ConnectionSettings;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

public class MockProvider extends AbstractConfigProvider {

    private HashMap<File, Properties> data = new HashMap<>();

    public MockProvider(File path) {
        super(path);
    }

    @Override
    public boolean createSettingWithProperties(String name, Properties properties) {
        data.put(new File(this.getPath(), name), properties);
        return true;
    }

    @Override
    public boolean createSettingWithProperties(File file, Properties properties) {
        data.put(file, properties);
        return true;
    }

    @Override
    public <T> T getProperty(String settingsName, String key) {
        Properties properties = data.get(new File(this.getPath(), settingsName));
        return (T) properties.get(key);
    }

    @Override
    public <T> T getProperty(File file, String key) {
        Properties properties = data.get(file);
        return (T) properties.get(key);
    }

    @Override
    public ConnectionSettings getConnectionSettings(String name) {
        ConnectionSettings connectionSettings = new ConnectionSettings(
                this.getProperty(name, "database"),
                this.getProperty(name, "hostname"),
                this.getProperty(name, "password"),
                Integer.valueOf(this.getProperty(name, "port")),
                this.getProperty(name, "username"));

        return connectionSettings;
    }

    @Override
    public ConnectionSettings getConnectionSettings(File file) {
        ConnectionSettings connectionSettings = new ConnectionSettings(
                this.getProperty(file, "database"),
                this.getProperty(file, "hostname"),
                this.getProperty(file, "password"),
                Integer.valueOf(this.getProperty(file, "port")),
                this.getProperty(file, "username"));

        return connectionSettings;
    }

    @Override
    public HashMap<String, ConnectionSettings> getAllSettingsFromDirectory(File file) {
        HashMap<String, ConnectionSettings> settings = new HashMap<>();

        for (File listFile : this.data.keySet()) {
            settings.put(listFile.getName(), this.getConnectionSettings(listFile));
        }

        return settings;
    }
}
