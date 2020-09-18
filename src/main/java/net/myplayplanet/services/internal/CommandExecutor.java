package net.myplayplanet.services.internal;

import net.myplayplanet.services.config.api.IConfigManager;
import net.myplayplanet.services.connection.api.IConnectionManager;
import net.myplayplanet.services.internal.api.AbstractCommand;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;

public class CommandExecutor {
    private final HashMap<String, AbstractCommand> hashMap;

    public CommandExecutor() {
        hashMap = new HashMap<>();
    }

    public void registerCommand(AbstractCommand cmd) {
        hashMap.put(cmd.getCommandName(), cmd);
    }

    public void setupCommands(IConfigManager iConfigManager, IConnectionManager IConnectionManager) {
        for (AbstractCommand value : hashMap.values()) {
            value.setupCommand(iConfigManager, IConnectionManager);
        }
    }

    public boolean canExecute(String[] args) {
        AbstractCommand cmd = hashMap.getOrDefault(String.join(" ", args), null);
        return cmd != null;
    }

    public void execute(String[] args) {
        AbstractCommand cmd = hashMap.getOrDefault(String.join(" ", args), null);
        Validate.notNull(cmd.getConfigManager());
        Validate.notNull(cmd.getConnectionManager());
        cmd.execute(args);
    }
}
