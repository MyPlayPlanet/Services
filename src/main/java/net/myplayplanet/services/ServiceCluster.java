package net.myplayplanet.services;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.provider.IResourceProvider;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.internal.CommandExecutor;
import net.myplayplanet.services.internal.exception.BadSetupException;
import net.myplayplanet.services.schedule.ScheduleService;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;

@Slf4j
public class ServiceCluster {
    private final HashMap<String, IService> serviceHashMap;
    private final CommandExecutor commandExecutor;

    private boolean started = false;

    protected ServiceCluster(IResourceProvider resourceProvider, boolean setupScheduler, boolean setupConfig, boolean setupConn) throws BadSetupException {
        serviceHashMap = new HashMap<>();
        commandExecutor = new CommandExecutor();

        if (setupScheduler) {
            addServices(false, new ScheduleService());
        }

        if (setupConfig) {
            addServices(new ConfigService(resourceProvider));
        }

        if (setupConn) {
            if (!setupConfig) {
                throw new BadSetupException("setup Config must be set to true if connection service should be setup");
            }
            addServices(false, new ConnectionService(this));
        }
    }

    public boolean runCommand(String[] args) {
        Validate.isTrue(started, "cluster must be started before it can run commands...");

        if (commandExecutor.canExecute(args)) {
            commandExecutor.execute(args);
            return true;
        } else {
            return false;
        }
    }

    public void startup() {
        this.startup(null, null);
    }

    public void startup(Runnable runnable) {
        this.startup(null, runnable);
    }

    public void startup(String[] args, Runnable runnable) {
        Validate.isTrue(!started, "cluster can not be started because startup Method was already called before.");

        for (IService value : this.serviceHashMap.values()) {
            value.init();
        }

        this.started = true;

        final ConfigService configService = this.get(ConfigService.class);
        final ConnectionService connectionService = this.get(ConnectionService.class);

        if (connectionService != null && configService != null) {
            commandExecutor.setupCommands(configService.getConfigManager(), connectionService.getConnectionManager());
        }

        boolean run = true;

        if (args != null && runCommand(args)) {
            run = false;
        }

        if (runnable != null && run) {
            runnable.run();
        }
    }

    public void addServices(final IService... IServices) {
        addServices(false, IServices);
    }

    public void addServices(boolean initiate, final IService... IServices) {
        for (IService iService : IServices) {
            if (this.serviceHashMap.containsKey(iService.getName())) {
                System.out.println("Service already initialized: " + iService.getName());
                continue;
            }

            this.serviceHashMap.put(iService.getName(), iService);
            if (initiate) {
                iService.init();
                iService.registerCommand(this.commandExecutor);
            }
        }
    }

    public void shutdownCluster() {
        for (IService value : serviceHashMap.values()) {
            value.disable();
        }
        serviceHashMap.clear();
    }

    public <T extends IService> T get(Class<T> clazz) {
        return (T) serviceHashMap.getOrDefault(clazz.getName(), null);
    }
}
