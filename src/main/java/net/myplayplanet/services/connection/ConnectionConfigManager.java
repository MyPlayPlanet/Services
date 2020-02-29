package net.myplayplanet.services.connection;


import net.myplayplanet.services.config.provider.IConfigManager;

import java.io.File;
import java.util.HashMap;

public class ConnectionConfigManager {

    private final IConfigManager configManager;

    public ConnectionConfigManager(IConfigManager configManager) {
        this.configManager = configManager;
    }

    public HashMap<String, ConnectionSettings> getConnectionSettings() {
        return getAllSettingsFromDirectory(configManager.getPath());
    }

    /**
     * @param name of the {@link ConnectionSettings} {@link File}
     * @return {@link ConnectionSettings} which are apply from the File
     */
    public ConnectionSettings getConnectionSettings(String name) {
        File setting = new File(configManager.getPath().getAbsolutePath() + "/" + name.toLowerCase() + ".properties");

        return getConnectionSettings(setting);
    }

    /**
     * @param file of which the {@link ConnectionSettings}
     * @return {@link ConnectionSettings} which are apply from the File
     */
    public ConnectionSettings getConnectionSettings(File file) {
        if (!configManager.exists(file)) {
            return null;
        }

        return new ConnectionSettings(
                configManager.getProperty(file, "database"),
                configManager.getProperty(file, "hostname"),
                configManager.getProperty(file, "password"),
                Integer.valueOf(configManager.getProperty(file, "port")),
                configManager.getProperty(file, "username"));
    }

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
