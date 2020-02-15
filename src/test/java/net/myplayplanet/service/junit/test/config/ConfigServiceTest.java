package net.myplayplanet.service.junit.test.config;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigManager;
import net.myplayplanet.services.config.ConfigService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ConfigServiceTest {
    private static ServiceCluster cluster;

    @BeforeAll
    public static void beforeAll() {
        cluster = new ServiceCluster();
        cluster.startupCluster(new File("home"), true);
    }

    @AfterAll
    public static void afterAll() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cluster.shutdownCluster();
    }

    @Test
    public void create_add_and_get_setting_test() {
        //Arrange
        String fileName = "example";

        Properties properties = new Properties();
        properties.setProperty("key", "value");
        ConfigManager configManager = cluster.get(ConfigService.class).getConfigManager();

        //Act
        try {
            configManager.createSettingWithProperties(fileName, properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = configManager.getProperty(fileName, "key");

        //Assert
        Assertions.assertEquals("value", result);
    }

}
