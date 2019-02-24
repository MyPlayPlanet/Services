package net.myplayplanet.services.config;

import lombok.AccessLevel;
import lombok.Getter;
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

    /**
     * Needs to be set before running Init
     */
    private File path;
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected ConfigManager configManager;

    @Override
    public void init() {
        validate(path == null, "Path needs to be set before Initializing ConfigService!");
        this.configManager = new ConfigManager(path);

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
                Log.getLog(log).info("created setting {setting}" , "redis-settings");
            }
            if (this.configManager.createSettingWithProperties("mysql-settings", mysqlProperties)) {
                Log.getLog(log).info("created setting {setting}" , "mysql-settings");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConnectionSettings getMySQLSettings(){
        return this.getConfigManager().getConnectionSettings("mysql-settings");
    }
    public ConnectionSettings getRedisSettings(){
        return this.getConfigManager().getConnectionSettings("redis-settings");
    }

    private static void validate(boolean expression, String message){
        if(expression){
            Log.getLog(log).error(new IllegalArgumentException(message), "error at config Service! {exceptionMessage}", message);
        }
    }
}
