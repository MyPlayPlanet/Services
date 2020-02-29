package net.myplayplanet.services.connection;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigService;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Properties;

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

        Properties redisProperties = new Properties();
        Properties mysqlProperties = new Properties();

        try {
            redisProperties.setProperty("hostname", Inet4Address.getLocalHost().getHostAddress());
            redisProperties.setProperty("database", "database");
            redisProperties.setProperty("port", "6379");
            redisProperties.setProperty("password", "foobared");
            redisProperties.setProperty("username", "username");

            mysqlProperties.setProperty("hostname", Inet4Address.getLocalHost().getHostAddress());
            mysqlProperties.setProperty("database", "database");
            mysqlProperties.setProperty("port", "3306");
            mysqlProperties.setProperty("password", "password");
            mysqlProperties.setProperty("username", "username");

            if (service.getConfigManager().createSettingWithProperties("redis-settings", redisProperties)) {
                System.out.println("created setting redis-settings");
            }
            if (service.getConfigManager().createSettingWithProperties("mysql-settings", mysqlProperties)) {
                System.out.println("created setting mysql-settings");
            }
        } catch (Exception e) {
            System.out.println("error setting properties: " + e.getMessage());
        }

        HashMap<String, ConnectionSettings> sqlSettings = new HashMap<>();
        HashMap<String, ConnectionSettings> redisSettings = new HashMap<>();

        ConnectionConfigManager connectionConfigManager = new ConnectionConfigManager(service.getConfigManager());

        HashMap<String, ConnectionSettings> settingsMap = connectionConfigManager.getConnectionSettings();
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