package net.myplayplanet.service.junit.test.config;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.service.junit.utils.ServiceInitializer;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigManager;
import net.myplayplanet.services.logger.Log;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Properties;
@Slf4j
public class ConfigServiceTest {
    @BeforeAll
    public static void beforeAll() {
        ServiceInitializer.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        ServiceInitializer.afterAll();
    }
    @Test
    public void create_add_and_get_setting_test() {
        //Arrange
        String fileName = "example";

        Properties properties = new Properties();
        properties.setProperty("key", "value");

        //Act
        try {
            ConfigManager.getInstance().createSettingWithProperties(fileName, properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = ConfigManager.getInstance().getProperty(fileName, "key");

        //Assert
        Assertions.assertEquals("value", result);
    }

}
