package net.myplayplanet.services.config.provider;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Predicate;

public interface IConfigManager {

    File getPath();

     boolean createSettingWithProperties(String name, Properties properties) throws IOException;

    boolean createSettingWithProperties(File file, Properties properties) throws IOException;

    default <T> T getPropertyFromResource(String resourceName, String key) throws IOException {
        String newResourceName = (resourceName.endsWith(".properties") ? resourceName : resourceName + ".properties");
        ClassPathResource classPathResource = new ClassPathResource(newResourceName);
        InputStream inputStream = classPathResource.getInputStream();

        Properties properties = new Properties();

        properties.load(inputStream);

        inputStream.close();

        return (T) properties.get(key);
    }

    <T> T getProperty(String settingsName, String key);

    <T> T getProperty(File file, String key);

    File[] getAllFilesInDirectory(File path, Predicate<String> filter);

    default boolean exists(String fileName) {
        return exists(new File(this.getPath(), fileName));
    }
    boolean exists(File fileName);
}
