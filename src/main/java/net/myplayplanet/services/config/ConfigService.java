package net.myplayplanet.services.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.connection.ConnectionSettings;
import net.myplayplanet.services.logger.Log;

import java.io.File;
import java.net.Inet4Address;
import java.util.Properties;

@Getter
@Setter
@Slf4j
public class ConfigService extends AbstractService {

    @NonNull
    private File path;
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected ConfigManager configManager;

    public ConfigService(File configPath) {
        this.path = configPath;
    }

    @Override
    public void init() {
        Log.getLog(log).info("Starting {service}...", "ConfigService");

        this.configManager = ConfigManager.createInstance(path);

        Properties redisProperties = new Properties();a
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
                Log.getLog(log).info("created setting {setting}" , "redis-settings");
            }
            if (this.configManager.createSettingWithProperties("mysql-settings", mysqlProperties)) {
                Log.getLog(log).info("created setting {setting}" , "mysql-settings");
            }
        } catch (Exception e) {
            Log.getLog(log).error(e, "error setting properties. {exceptionMessage} " + e.getMessage());
        }
    }

    @Override
    public void disable() {
        Log.getLog(log).info("Shutting down {service}...", "ConfigService");
    }

    public ConnectionSettings getMySQLSettings(){
        //return new ConnectionSettings("minecraft" ,"195.201.61.79", "<insert password>", 3307, "minecraft");
        return this.getConfigManager().getConnectionSettings("mysql-settings");
    }
    public ConnectionSettings getRedisSettings(){
        //return new ConnectionSettings("" ,"localhost", "", 6379, "");
        return this.getConfigManager().getConnectionSettings("redis-settings");
    }
}
