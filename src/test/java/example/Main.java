package example;

import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.logger.Log;
import net.myplayplanet.services.logger.sinks.MySQLEntry;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ServiceCluster.startupCluster(new File("D:\\temp\\mpp"));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ServiceCluster.shutdownCluster();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}