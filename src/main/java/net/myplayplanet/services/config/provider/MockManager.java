package net.myplayplanet.services.config.provider;

import lombok.Getter;
import net.myplayplanet.services.connection.ConnectionSettings;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

public class MockManager implements IConfigManager {

    private HashMap<File, Properties> data = new HashMap<>();
    @Getter
    private File path;

    public MockManager(File path) {
        this.path = path;
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
    public boolean exists(File file) {
        return data.containsKey(file);
    }
}
