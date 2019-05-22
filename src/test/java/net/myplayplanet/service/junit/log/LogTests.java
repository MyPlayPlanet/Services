package net.myplayplanet.service.junit.log;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.logger.Log;
import net.myplayplanet.services.logger.LogLevel;
import net.myplayplanet.services.logger.LoggerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
public class LogTests {
    @BeforeAll
    public static void beforeAll() {
        ServiceCluster.setDebug(true);
        ServiceCluster.addServices(true, new LoggerService());
    }

    @Test
    public void simple_log_message() {
        //the result should be something like:
        //[main] INFO net.myplayplanet.service.junit.log.LogTests  - test message!
        Log.getLog(log).info("test message!"); //todo automate checking this
    }

    @Test
    public void sdafsd1() {
        long time = System.currentTimeMillis();
        int c = 1;
        for (int i = 0; i < 1000; i++) {
            Log.getLog(log).log(null, LogLevel.INFO, ""+i);
        }
        System.out.println(System.currentTimeMillis()-time);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
