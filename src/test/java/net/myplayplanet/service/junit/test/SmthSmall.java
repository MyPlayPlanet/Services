package net.myplayplanet.service.junit.test;

import net.myplayplanet.service.junit.utils.ServiceInitializer;
import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.connection.ConnectionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SmthSmall {
    @BeforeAll
    public static void beforeAll() {
        ServiceInitializer.beforeAll();
    }
    @AfterAll
    public static void afterAll() {
        ServiceInitializer.afterAll();
    }
    //@Test
    public void test() {
        ConnectionManager instance = ConnectionManager.getInstance();
        Connection mySQLConnection = instance.getMySQLConnection();
        try {
            PreparedStatement statement = mySQLConnection.prepareStatement("SELECT * FROM `user_settings`");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                System.out.println("got: " + set.getString("uuid") +" -> " + set.getInt("setting_state"));
            }
            statement.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                mySQLConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        Cache<Integer, String> cache = new Cache<>(
                "normal-cache",
                1, 70,
                integer -> { //Must have
                    return integer + " " + UUID.randomUUID().toString();
                });

        String r = cache.get(3);
        System.out.println(r);
        try {
            Thread.sleep(60001);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(cache.get(3));

    }
}
