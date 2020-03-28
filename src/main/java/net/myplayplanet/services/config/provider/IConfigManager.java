package net.myplayplanet.services.config.provider;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Predicate;

public interface IConfigManager {

    File getPath();

    /**
     * @param name       The name of the File which should be created
     * @param properties {@link Properties}
     * @return No further information provided
     * @throws IOException No further information provided
     */
     boolean createSettingWithProperties(String name, Properties properties) throws IOException;

    /**
     * @param file       The File which should be created
     * @param properties {@link Properties}
     * @return No further information provided
     * @throws IOException No further information provided
     */
    boolean createSettingWithProperties(File file, Properties properties) throws IOException;

    default <T> T getPropertyFromResource(String resourceName, String key) throws IOException {
        String newResourceName = (resourceName.endsWith(".properties") ? resourceName : resourceName + ".properties");
        File file = new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource(newResourceName)).getFile()
        );

        InputStream inputStream = new FileInputStream(file);

        Properties properties = new Properties();

        properties.load(inputStream);

        inputStream.close();

        return (T) properties.get(key);
    }

    /**
     * @param settingsName The File Name from which you apply the Property
     * @param key          The key from which you apply the Property
     * @param <T>          The Type you want to apply back
     * @return The Property in the Type you want
     */
    <T> T getProperty(String settingsName, String key);
    /**
     * @param file The File from which you apply the Property
     * @param key  The key from which you apply the Property
     * @param <T>  The Type you want to apply back
     * @return The Property in the Type you want
     */
    <T> T getProperty(File file, String key);

    File[] getAllFilesInDirectory(File path, Predicate<String> filter);

    default boolean exists(String fileName) {
        return exists(new File(this.getPath(), fileName));
    }
    boolean exists(File fileName);
}
