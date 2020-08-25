package net.myplayplanet.services.internal;

import net.myplayplanet.services.config.provider.IConfigManager;
import net.myplayplanet.services.config.provider.IResourceProvider;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.connection.dbversion.UpdateManager;
import net.myplayplanet.services.connection.dbversion.exception.SetupNotSuccessfulException;
import net.myplayplanet.services.connection.provider.MySqlManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractService {
    private UpdateManager updateManager;

    public AbstractService(IConfigManager configManager, ConnectionManager connectionManager) {
        IResourceProvider iResourceProvider = IResourceProvider.getResourceProvider();

        try {
            this.updateManager = new UpdateManager(configManager, connectionManager, iResourceProvider);
        } catch (SQLException | IOException | SetupNotSuccessfulException e) {
            System.out.println("error while basic setup:" + e.getMessage());
            e.printStackTrace();
            System.exit(130);
        }

        try (Connection connection = connectionManager.get(MySqlManager.class).get()) {
            final int currentVersion = updateManager.getCurrentVersion(connection);
            final int newestVersion = updateManager.getNewestVersion();

            if (newestVersion != currentVersion) {
                System.out.println("[ERROR] ---------------------------");
                System.out.println("[ERROR]");
                System.out.println("[ERROR] your database is out of date: ");
                System.out.println("[ERROR] current Database Version: " + currentVersion);
                System.out.println("[ERROR] newest Script found: " + newestVersion);
                System.out.println("[ERROR]");
                System.out.println("[ERROR] ---------------------------");
                System.exit(120);
            }

        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
