package net.myplayplanet.services.config.provider;

import lombok.Getter;
import net.myplayplanet.services.connection.ConnectionSettings;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public abstract class AbstractConfigProvider {

    @Getter
    private File path;

    public AbstractConfigProvider(File path) {
        this.path = path;
    }

    /**
     * @param name       The name of the File which should be created
     * @param properties {@link Properties}
     * @return No further information provided
     * @throws IOException No further information provided
     */
    public abstract boolean createSettingWithProperties(String name, Properties properties) throws IOException;

    /**
     * @param file       The File which should be created
     * @param properties {@link Properties}
     * @return No further information provided
     * @throws IOException No further information provided
     */
    public abstract boolean createSettingWithProperties(File file, Properties properties) throws IOException;


    /**
     * @param settingsName The File Name from which you apply the Property
     * @param key          The key from which you apply the Property
     * @param <T>          The Type you want to apply back
     * @return The Property in the Type you want
     */
    public abstract <T> T getProperty(String settingsName, String key);

    /**
     * @param file The File from which you apply the Property
     * @param key  The key from which you apply the Property
     * @param <T>  The Type you want to apply back
     * @return The Property in the Type you want
     */
    public abstract <T> T getProperty(File file, String key);

    /**
     * @param name of the {@link ConnectionSettings} {@link File}
     * @return {@link ConnectionSettings} which are apply from the File
     */
    public abstract ConnectionSettings getConnectionSettings(String name);

    /**
     * @param file of which the {@link ConnectionSettings}
     * @return {@link ConnectionSettings} which are apply from the File
     */
    public abstract ConnectionSettings getConnectionSettings(File file);

    /**
     * @param file of which the {@link ConnectionSettings}
     * @return {@link ConnectionSettings} which are apply from the File
     */
    public abstract HashMap<String, ConnectionSettings> getAllSettingsFromDirectory(File file);

}
