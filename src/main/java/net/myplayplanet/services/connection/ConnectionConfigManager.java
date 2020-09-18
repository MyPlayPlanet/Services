package net.myplayplanet.services.connection;


import net.myplayplanet.services.config.api.IConfigManager;

import java.io.File;
import java.util.HashMap;

public class ConnectionConfigManager {

    private final IConfigManager configManager;

    public ConnectionConfigManager(IConfigManager configManager) {
        this.configManager = configManager;
    }

    public HashMap<String, ConnectionSetting> getConnectionSettings() {
        return getAllSettingsFromDirectory(configManager.getPath());
    }

    public ConnectionSetting getConnectionSettings(String name) {
        File setting = new File(configManager.getPath().getAbsolutePath() + "/" + name.toLowerCase() + ".properties");

        return getConnectionSettings(setting);
    }

    public ConnectionSetting getConnectionSettings(File file) {
        if (!configManager.exists(file)) {
            return null;
        }

        return new ConnectionSetting(
                configManager.getProperty(file, "database"),
                configManager.getProperty(file, "hostname"),
                configManager.getProperty(file, "password"),
                Integer.valueOf(configManager.getProperty(file, "port")),
                configManager.getProperty(file, "username"));
    }

    public HashMap<String, ConnectionSetting> getAllSettingsFromDirectory(File file) {
        HashMap<String, ConnectionSetting> connectionSettings = new HashMap<>();

        for (File listFile : this.configManager.getAllFilesInDirectory(file, (name) -> name.endsWith("settings.properties"))) {
            ConnectionSetting settings = this.getConnectionSettings(listFile);

            if (settings != null) {
                connectionSettings.put(listFile.getName(), settings);
            }
        }
        return connectionSettings;
    }
}
