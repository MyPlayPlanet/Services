package net.myplayplanet.services.config;

import com.google.common.io.Files;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.ConnectionSettings;
import net.myplayplanet.services.logger.Log;

import java.io.*;
import java.util.Properties;

@Getter(AccessLevel.PRIVATE)
@Slf4j
public class ConfigManager {

    private File path;

    public ConfigManager(File path) {
        Log.getLog(log).debug("creating Config Manager on File {path}", path.getPath());
        this.path = path;
    }

    /**
     * @param name       The name of the File which should be created
     * @param properties {@link Properties}
     * @throws IOException
     */
    public boolean createSettingWithProperties(String name, Properties properties) throws IOException {
        File settings = new File(this.path.getAbsolutePath() + "/" + name.toLowerCase() + ".properties");

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
     * @param file       The File which should be created
     * @param properties {@link Properties}
     * @throws IOException
     */
    public void createSettingWithProperties(File file, Properties properties) throws IOException {
        if (!(file.exists())) {
            file.createNewFile();
            this.setProperties(file, properties);
        }
    }

    /**
     * @param settingsName The File Name from which you get the Property
     * @param key          The key from which you get the Property
     * @param <T>          The Type you want to get back
     * @return The Property in the Type you want
     */
    public <T> T getProperty(String settingsName, String key) {
        File setting = new File(this.path.getAbsolutePath() + "/" + settingsName.toLowerCase() + ".properties");

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
     * @param file The File from which you get the Property
     * @param key  The key from which you get the Property
     * @param <T>  The Type you want to get back
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
     * @return {@link ConnectionSettings} which are get from the File
     */
    public ConnectionSettings getConnectionSettings(String name) {
        File setting = new File(this.path.getAbsolutePath() + "/" + name.toLowerCase() + ".properties");

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
     * @return {@link ConnectionSettings} which are get from the File
     */
    public ConnectionSettings getConnectionSettings(File file) {
        if (!(file.exists())) {
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
}
