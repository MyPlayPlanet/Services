package net.myplayplanet.service.junit.test;

import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigManager;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.connection.ConnectionSettings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class ServiceClusterTest {
    private static ServiceCluster cluster;
    @BeforeAll
    public static void beforeAll() {
        File file = new File("file");
        cluster = new ServiceCluster();
        cluster.startupCluster(file, true);
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
        ConnectionSettings setting = cluster.get(ConfigService.class).getConfigManager().getConnectionSettings("mysql-settings");

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
        ConnectionSettings setting = cluster.get(ConfigService.class).getConfigManager().getConnectionSettings("redis-settings");

        //Assert
        Assertions.assertEquals(hostAddress, setting.getHostname());
        Assertions.assertEquals("database", setting.getDatabase());
        Assertions.assertEquals(6379, (int) setting.getPort());
        Assertions.assertEquals("foobared", setting.getPassword());
        Assertions.assertEquals("username", setting.getUsername());
    }
}
