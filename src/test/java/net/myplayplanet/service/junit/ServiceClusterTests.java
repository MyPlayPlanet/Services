package net.myplayplanet.service.junit;

import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigManager;
import net.myplayplanet.services.connection.ConnectionSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Properties;

public class ServiceClusterTests {

    @BeforeAll
    public static void beforeAll() {
        ServiceCluster.setDebug(true);
        ServiceCluster.startupCluster(new File("home"));
    }

    @Test
    public void createSettingsTests() {
        Properties properties = new Properties();
        properties.setProperty("key", "value");
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
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ConnectionSettings redisSettings = new ConnectionSettings(
                redisProperties.getProperty("database"),
                redisProperties.getProperty("hostname"),
                redisProperties.getProperty("password"),
                Integer.valueOf(redisProperties.getProperty("port")),
                redisProperties.getProperty("username"));
        ConnectionSettings mysqlSettings = new ConnectionSettings(
                mysqlProperties.getProperty("database"),
                mysqlProperties.getProperty("hostname"),
                mysqlProperties.getProperty("password"),
                Integer.valueOf(mysqlProperties.getProperty("port")),
                mysqlProperties.getProperty("username"));

        try {
            ConfigManager.getInstance().createSettingWithProperties("example", properties);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(ConfigManager.getInstance().getProperty("example", "key"), "value");
        Assertions.assertEquals(ConfigManager.getInstance().getConnectionSettings("redis-settings"), redisSettings);
        Assertions.assertEquals(ConfigManager.getInstance().getConnectionSettings("mysql-settings"), mysqlSettings);
    }


}
