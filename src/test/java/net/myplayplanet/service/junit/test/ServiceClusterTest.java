package net.myplayplanet.service.junit.test;

import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigManager;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.connection.ConnectionSettings;
import net.myplayplanet.services.logger.LoggerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Properties;

public class ServiceClusterTest {

    @BeforeAll
    public static void beforeAll() {
        ServiceCluster.setDebug(true);
        ServiceCluster.addServices(true, new LoggerService());
        ServiceCluster.addServices(true, new ConfigService(new File("home")));
    }

    @AfterAll
    public static void afterAll() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create_and_get_mysql_settings_test() {
        //Arrange
        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //Act
        ConnectionSettings setting = ConfigManager.getInstance().getConnectionSettings("mysql-settings");

        //Assert
        Assertions.assertEquals(hostAddress, setting.getHostname());
        Assertions.assertEquals("database", setting.getDatabase());
        Assertions.assertEquals(3306, (int) setting.getPort());
        Assertions.assertEquals("password", setting.getPassword());
        Assertions.assertEquals("username", setting.getUsername());
    }

    @Test
    public void create_and_get_redis_settings_test() {
        //Arrange
        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //Act
        ConnectionSettings setting = ConfigManager.getInstance().getConnectionSettings("redis-settings");

        //Assert
        Assertions.assertEquals(hostAddress, setting.getHostname());
        Assertions.assertEquals("database", setting.getDatabase());
        Assertions.assertEquals(6379, (int) setting.getPort());
        Assertions.assertEquals("foobared", setting.getPassword());
        Assertions.assertEquals("username", setting.getUsername());
    }
}
