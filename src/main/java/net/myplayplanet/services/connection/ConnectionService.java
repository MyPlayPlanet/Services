package net.myplayplanet.services.connection;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigService;

import java.util.HashMap;

@Slf4j
public class ConnectionService extends AbstractService {
    HashMap<String, ConnectionManager> managerHashMap;
    private boolean debug;

    public ConnectionService(ServiceCluster cluster, boolean debug) {
        super(cluster);
        this.debug = debug;
        managerHashMap = new HashMap<>();
    }

    @Override
    public void init() {
        System.out.println("starting ConnectionService");

        ConfigService service = this.getCluster().get(ConfigService.class);

        HashMap<String, ConnectionSettings> sqlSettings = new HashMap<>();
        HashMap<String, ConnectionSettings> redisSettings = new HashMap<>();

        HashMap<String, ConnectionSettings> settingsMap = service.getConnectionSettings();
        for (String string : settingsMap.keySet()) {
            if (string.endsWith("mysql-settings.properties")) {
                String replace = (string.equalsIgnoreCase("mysql-settings.properties")) ? "minecraft" : string.replace("mysql-settings.properties", "");
                sqlSettings.put(replace, settingsMap.get(string));
            } else if (string.endsWith("redis-settings.properties")) {
                String replace = (string.equalsIgnoreCase("redis-settings.properties")) ? "minecraft" : string.replace("redis-settings.properties", "");
                redisSettings.put(replace, settingsMap.get(string));
            }
        }

        for (String settingName : sqlSettings.keySet()) {
            ConnectionSettings sqlSetting = sqlSettings.getOrDefault(settingName, null);
            ConnectionSettings redisSetting = redisSettings.getOrDefault(settingName, null);

            assert sqlSetting != null : "SQL setting for name " + settingName + " could not be found.";
            assert redisSetting != null : "Redis setting for name " + settingName + " could not be found.";

            managerHashMap.put(settingName, new ConnectionManager(redisSetting, sqlSetting, debug));
            System.out.println("Created ConnectionManager with [" + settingName.toUpperCase() + "] ConnectionSettings");
        }
    }

    public ConnectionManager getConnectionManager(String name) {
        return managerHashMap.getOrDefault(name, null);
    }

    public ConnectionManager getConnectionManager() {
        return getConnectionManager("minecraft");
    }
}