package net.myplayplanet.services.internal.api;

import net.myplayplanet.services.config.api.IConfigManager;
import net.myplayplanet.services.connection.api.IConnectionManager;

public abstract class AbstractCommand {
    private IConnectionManager IConnectionManager;
    private IConfigManager configManager;

    public void setupCommand(IConfigManager iConfigManager, IConnectionManager IConnectionManager) {
        this.configManager = iConfigManager;
        this.IConnectionManager = IConnectionManager;
    }

    public abstract void execute(String[] args);

    public abstract String getCommandName();

    public IConnectionManager getConnectionManager() {
        return IConnectionManager;
    }

    public IConfigManager getConfigManager() {
        return configManager;
    }
}
