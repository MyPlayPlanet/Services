package net.myplayplanet.service.junit.test.log;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.service.junit.utils.ServiceInitializer;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.logger.Log;
import org.junit.jupiter.api.*;

@Slf4j
public class LogTests {

    @BeforeAll
    public static void beforeAll() {
        ServiceInitializer.beforeAll();
    }

    @AfterAll
    public static void afterAll() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ServiceCluster.shutdownCluster();
    }
    @BeforeEach
    public void beforeEach() {
        Log.getLog(log).info("============== Before ==============");
    }

    @AfterEach
    public void afterEach() {
        Log.getLog(log).info("============== After ==============");
    }

    @Test
    public void simple_log_message() {
        //the result should be something like:
        //[main] INFO LogTests  - test message!
        Log.getLog(log).info("test message!"); //todo automate checking this
    }
}
