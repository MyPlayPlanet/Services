package net.myplayplanet.services.connection.dbversion;

import net.myplayplanet.services.config.api.IResourceProvider;
import net.myplayplanet.services.connection.dbversion.exception.SetupNotSuccessfulException;
import net.myplayplanet.services.internal.api.AbstractCommand;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateCommand extends AbstractCommand {
    private final IResourceProvider resourceProvider;

    public UpdateCommand(IResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("start updating command...");
        UpdateManager updateManager = null;
        try {
            updateManager = new UpdateManager(this.getConfigManager(), this.getConnectionManager(), resourceProvider);
        } catch (SQLException | IOException | SetupNotSuccessfulException e) {
            System.out.println("error while basic setup:" + e.getMessage());
            e.printStackTrace();
            System.exit(130);
        }

        updateManager.update();

        System.out.println("finished updating command...");
    }

    @Override
    public String getCommandName() {
        return "update";
    }
}
