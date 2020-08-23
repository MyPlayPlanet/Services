package net.myplayplanet.services.config.provider.config;

import lombok.Getter;
import net.myplayplanet.services.config.provider.IConfigManager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Predicate;

public class MockConfigManager implements IConfigManager {

    private HashMap<File, Properties> data = new HashMap<>();
    @Getter
    private File path;

    public MockConfigManager(File path) {
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
    public File[] getAllFilesInDirectory(File path, Predicate<String> filter) {
        return data.keySet().stream().filter(file -> filter.test(file.getName())).toArray(File[]::new);
    }

    @Override
    public File[] getAllFilesInDirectory(Predicate<String> filter) {
        return this.getAllFilesInDirectory(null, filter);
    }

    @Override
    public boolean exists(File file) {
        return data.containsKey(file);
    }
}
