package net.myplayplanet.service.junit.test.config;

import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.connection.provider.MySqlManager;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {
        ServiceCluster cluster = new ServiceCluster();
        File path = new File("MyPlayPlanet-Services");
        cluster.startupCluster(path, false);

        ConnectionManager connectionManager = cluster.get(ConnectionService.class).getLoader();


        try (Connection conn = connectionManager.get(MySqlManager.class).get()){

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
