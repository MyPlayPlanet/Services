package net.myplayplanet.services.config.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Predicate;

public interface IConfigManager {

    File getPath();

    boolean createSettingWithProperties(String name, Properties properties) throws IOException;

    boolean createSettingWithProperties(File file, Properties properties) throws IOException;

    IResourceProvider getResourceProvider();

    default <T> T getPropertyFromResource(String resourceName, String key) throws IOException, FileNotFoundException {
        String newResourceName = (resourceName.endsWith(".properties") ? resourceName : resourceName + ".properties");

        InputStream inputStream = this.getResourceProvider().getResourceFile(newResourceName);

        Properties properties = new Properties();

        properties.load(inputStream);

        inputStream.close();

        return (T) properties.get(key);
    }

    <T> T getProperty(String settingsName, String key);

    <T> T getProperty(File file, String key);

    File[] getAllFilesInDirectory(File path, Predicate<String> filter);

    File[] getAllFilesInDirectory(Predicate<String> filter);

    default boolean exists(String fileName) {
        return exists(new File(this.getPath(), fileName));
    }

    boolean exists(File fileName);
}
