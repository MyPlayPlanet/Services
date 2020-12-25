package net.myplayplanet.service.junit.test.config;

import net.myplayplanet.services.api.IServiceCluster;
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

public class ConfigServiceTest {
    private static IServiceCluster cluster;

    @BeforeAll
    public static void beforeAll() throws BadSetupException {
        cluster = new JavaClusterBuilder().withConfig().mock(true, "").build();
        cluster.startup();
    }

    @AfterAll
    public static void afterAll() throws InterruptedException {
        Thread.sleep(1000);
        cluster.shutdownCluster();
    }

    @Test
    public void create_add_and_get_setting_test() throws IOException {
        //Arrange
        String fileName = "example";

        Properties properties = new Properties();
        properties.setProperty("key", "value");
        IConfigManager configManager = cluster.get(ConfigService.class).getConfigManager();

        //Act
        configManager.createSettingWithProperties(fileName, properties);

        String result = configManager.getProperty(fileName, "key");

        //Assert
        Assertions.assertEquals("value", result);
    }

}
