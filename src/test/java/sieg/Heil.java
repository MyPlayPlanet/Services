package sieg;

import net.myplayplanet.services.ServiceCluster;

import java.io.File;

public class Heil {

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