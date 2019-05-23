package net.myplayplanet.service.junit.utils;

import lombok.Getter;
import lombok.Setter;
import net.myplayplanet.services.ServiceCluster;

import java.io.File;

public class ServiceInitializer {
    @Setter
    @Getter
    private static boolean init;

    public static void beforeAll() {
        if (init) {
            return;
        }

        ServiceCluster.setDebug(true);
        ServiceCluster.startupCluster(new File("home"));
        init = true;
    }

    public static void afterAll() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ServiceCluster.shutdownCluster();
    }
}
