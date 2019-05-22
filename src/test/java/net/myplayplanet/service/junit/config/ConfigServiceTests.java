package net.myplayplanet.service.junit.config;

import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigManager;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.logger.LoggerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class ConfigServiceTests {
    @BeforeAll
    public static void beforeAll() {
        ServiceCluster.setDebug(true);
        ServiceCluster.addServices(true, new LoggerService());
        ServiceCluster.addServices(true, new ConfigService(new File("home")));
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
