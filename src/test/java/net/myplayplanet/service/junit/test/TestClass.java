package net.myplayplanet.service.junit.test;

import net.myplayplanet.services.ServiceCluster;

import java.io.File;

public class TestClass {

    public static void main(String[] args) {
        ServiceCluster.startupCluster(new File("MyPlayPlanet-Services"));
    }

}
