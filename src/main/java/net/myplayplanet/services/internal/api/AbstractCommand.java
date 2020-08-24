package net.myplayplanet.services.internal.api;

import net.myplayplanet.services.config.provider.IConfigManager;
import net.myplayplanet.services.connection.ConnectionManager;

public abstract class AbstractCommand {
    private ConnectionManager connectionManager;
    private IConfigManager configManager;

    public void setupCommand(IConfigManager iConfigManager, ConnectionManager connectionManager) {
        this.configManager = iConfigManager;
        this.connectionManager = connectionManager;
    }

    public abstract void execute(String[] args);
    public abstract String getCommandName();

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public IConfigManager getConfigManager() {
        return configManager;
    }
}
