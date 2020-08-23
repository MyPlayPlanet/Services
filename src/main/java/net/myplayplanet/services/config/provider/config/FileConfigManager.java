package net.myplayplanet.services.config.provider.config;

import com.google.common.io.Files;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.config.provider.IConfigManager;

import java.io.*;
import java.util.Properties;
import java.util.function.Predicate;

@Slf4j
public class FileConfigManager implements IConfigManager {
    @Getter
    private File path;

    public FileConfigManager(File path) {
        this.path = path;
    }

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

    public boolean createSettingWithProperties(File file, Properties properties) throws IOException {
        if (!(file.exists())) {
            Files.createParentDirs(file);
            file.createNewFile();
            this.setProperties(file, properties);
            return true;
        }
        return false;
    }

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
    @Override
    public File[] getAllFilesInDirectory(File path, Predicate<String> filter) {
        return path.listFiles((dir, name) -> filter.test(name));
    }

    @Override
    public File[] getAllFilesInDirectory(Predicate<String> filter) {
        return this.getAllFilesInDirectory(new File(this.getPath().getAbsolutePath()), filter);
    }

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
