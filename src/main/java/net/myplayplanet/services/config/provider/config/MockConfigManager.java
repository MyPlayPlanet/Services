package net.myplayplanet.services.config.provider.config;

import lombok.Getter;
import net.myplayplanet.services.config.api.IConfigManager;
import net.myplayplanet.services.config.api.IResourceProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Predicate;

public class MockConfigManager implements IConfigManager {

    private final HashMap<File, Properties> data = new HashMap<>();
    @Getter
    private final File path;
    private final IResourceProvider resourceProvider;

    public MockConfigManager(File path, IResourceProvider resourceProvider) {
        this.path = path;
        this.resourceProvider = resourceProvider;
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
    public IResourceProvider getResourceProvider() {
        return this.resourceProvider;
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
