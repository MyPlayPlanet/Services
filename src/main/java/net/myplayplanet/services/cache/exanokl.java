package net.myplayplanet.services.cache;

import net.myplayplanet.services.connection.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class exanokl implements ISQLCacheProvider<UUID> {
    @Override
    public void update(Cache<UUID> object) {
        object.getCachedObjects().forEach((uuid, uuidCacheObject) -> {
            try {
                PreparedStatement statement = ConnectionManager.getInstance().getMySQLConnection().prepareStatement("");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void updateLater(Cache<UUID> object) {
        object.getCachedObjects().forEach((uuid, uuidCacheObject) -> {
            //Mysql
        });
    }
}
