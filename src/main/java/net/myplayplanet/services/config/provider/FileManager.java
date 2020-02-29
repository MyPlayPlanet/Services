package net.myplayplanet.services.config.provider;

import com.google.common.io.Files;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.ConnectionSettings;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

@Slf4j
public class FileManager implements IConfigManager {
    @Getter
    private File path;

    public FileManager(File path) {
        this.path = path;
    }

    /**
     * @param name       The name of the File which should be created
     * @param properties {@link Properties}
     * @throws IOException No further information provided
     */
    public boolean createSettingWithProperties(String name, Properties properties) throws IOException {
        File settings = new File(this.getPath().getAbsolutePath() + "/" + name.toLowerCase() + ".properties");

        if (!(settings.exists())) {
            Files.createParentDirs(settings);
            settings.createNewFile();
            this.setProperties(settings, properties);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param file       The File which should be created
     * @param properties {@link Properties}
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

    @Override
    public boolean exists(File file) {
        return file.exists();
    }
}
