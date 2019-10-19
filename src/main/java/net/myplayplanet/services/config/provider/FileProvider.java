package net.myplayplanet.services.config.provider;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.ConnectionSettings;
import net.myplayplanet.services.logger.Log;

import java.io.*;
import java.util.*;

@Slf4j
public class FileProvider extends AbstractConfigProvider {
    public FileProvider(File path) {
        super(path);
    }

    /**
     * @param name         The name of the File which should be created
     * @param properties   {@link Properties}
     * @throws IOException No further information provided
     */
    public boolean createSettingWithProperties(String name, Properties properties) throws IOException {
        File settings = new File(this.getPath().getAbsolutePath() + "/" + name.toLowerCase() + ".properties");

        if (!(settings.exists())) {
            Files.createParentDirs(settings);
            settings.createNewFile();
            this.setProperties(settings, properties);
            return true;
        }else {
            return false;
        }
    }

    /**
     * @param file         The File which should be created
     * @param properties   {@link Properties}
     * @throws IOException No further information provided
     */
    public boolean createSettingWithProperties(File file, Properties properties) throws IOException {
        if (!(file.exists())) {
            Files.createParentDirs(file);
            file.createNewFile();
            this.setProperties(file, properties);
            return true;
        }
        return false;
    }

    /**
     * @param settingsName The File Name from which you apply the Property
     * @param key          The key from which you apply the Property
     * @param <T>          The Type you want to apply back
     * @return The Property in the Type you want
     */
    public <T> T getProperty(String settingsName, String key) {
        File setting = new File(this.getPath().getAbsolutePath() + "/" + settingsName.toLowerCase() + ".properties");

        if (!(setting.exists())) {
            return null;
        }

        try {
            InputStream inputStream = new FileInputStream(setting);

            Properties properties = new Properties();

            properties.load(inputStream);

            inputStream.close();

            return (T) properties.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param file The File from which you apply the Property
     * @param key  The key from which you apply the Property
     * @param <T>  The Type you want to apply back
     * @return The Property in the Type you want
     */
    public <T> T getProperty(File file, String key) {
        if (!(file.exists())) {
            return null;
        }
        try {
            InputStream inputStream = new FileInputStream(file);

            Properties properties = new Properties();

            properties.load(inputStream);

            inputStream.close();

            return (T) properties.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param file       the File in which the Settings are set
     * @param properties the Properties which you want to set
     */
    private void setProperties(File file, Properties properties) {
        OutputStream outputStream = null;

        try {

            outputStream = new FileOutputStream(file);
            properties.store(outputStream, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param name of the {@link ConnectionSettings} {@link File}
     * @return {@link ConnectionSettings} which are apply from the File
     */
    public ConnectionSettings getConnectionSettings(String name) {
        File setting = new File(this.getPath().getAbsolutePath() + "/" + name.toLowerCase() + ".properties");

        if (!(setting.exists())) {
            return null;
        }

        ConnectionSettings connectionSettings = new ConnectionSettings(
                this.getProperty(name, "database"),
                this.getProperty(name, "hostname"),
                this.getProperty(name, "password"),
                Integer.valueOf(this.getProperty(name, "port")),
                this.getProperty(name, "username"));
        return connectionSettings;
    }

    /**
     * @param file of which the {@link ConnectionSettings}
     * @return {@link ConnectionSettings} which are apply from the File
     */
    public ConnectionSettings getConnectionSettings(File file) {
        if (!(file.exists())) {
            return null;
        }

        if (!file.getName().toLowerCase().contains("redis") && !file.getName().toLowerCase().contains("mysql")) {
            Log.getLog(log).warning("Settings file with name {name} was not recognised as a SQL or Redis Setting.", file.getName());
            return null;
        }

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
        HashMap<String, ConnectionSettings> connectionSettings = new HashMap<>();

        for (File listFile : file.listFiles((dir, name) -> name.endsWith("settings.properties"))) {
            ConnectionSettings settings = this.getConnectionSettings(listFile);

            if (settings != null) {
                connectionSettings.put(listFile.getName(), settings);
            }
        }
        return connectionSettings;
    }
}
