package net.myplayplanet.services.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.connection.ConnectionSettings;

import java.io.File;
import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Properties;

@Getter
@Setter
@Slf4j
public class ConfigService extends AbstractService {

    @Setter(AccessLevel.PROTECTED)
    protected ConfigManager configManager;
    @NonNull
    private File path;

    public ConfigService(ServiceCluster cluster, File configPath, ConfigManager configManager) {
        super(cluster);
        this.path = configPath;
        this.configManager = configManager;
    }

    @Override
    public void init() {
        System.out.println("Starting ConfigService...");

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

            if (this.configManager.createSettingWithProperties("redis-settings", redisProperties)) {
                System.out.println("created setting redis-settings");
            }
            if (this.configManager.createSettingWithProperties("mysql-settings", mysqlProperties)) {
                System.out.println("created setting mysql-settings");
            }
        } catch (Exception e) {
            System.out.println("error setting properties: " + e.getMessage());
        }
    }

    public ConnectionSettings getConnectionSettings(String name) {
        return this.getConnectionSettings().get(name);
    }

    public HashMap<String, ConnectionSettings> getConnectionSettings() {
        return this.getConfigManager().getAllSettingsFromDirectory(this.getPath());
    }

}
