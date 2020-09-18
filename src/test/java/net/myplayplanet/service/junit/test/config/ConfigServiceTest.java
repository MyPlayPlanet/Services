package net.myplayplanet.service.junit.test.config;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.cluster.JavaServiceCluster;
import net.myplayplanet.services.cluster.JavaClusterBuilder;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.api.IConfigManager;
import net.myplayplanet.services.internal.exception.BadSetupException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ConfigServiceTest {
    private static JavaServiceCluster cluster;

    @BeforeAll
    public static void beforeAll() throws BadSetupException {
        cluster = new JavaClusterBuilder().withConfig().mock(true, "").build();
        cluster.startup();
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
        IConfigManager configManager = cluster.get(ConfigService.class).getConfigManager();

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
