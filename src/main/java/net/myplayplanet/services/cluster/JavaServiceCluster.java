package net.myplayplanet.services.cluster;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.api.IService;
import net.myplayplanet.services.api.IServiceCluster;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.api.IResourceProvider;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.internal.CommandExecutor;
import net.myplayplanet.services.internal.exception.BadSetupException;
import net.myplayplanet.services.schedule.ScheduleService;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;

@Slf4j
public class JavaServiceCluster implements IServiceCluster {
    private final HashMap<String, IService> serviceHashMap;
    private final CommandExecutor commandExecutor;

    private boolean started = false;

    protected JavaServiceCluster(IResourceProvider resourceProvider, boolean setupScheduler, boolean setupConfig, boolean setupConn) throws BadSetupException {
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
            addServices(false, new ConnectionService(this, resourceProvider));
        }
    }

    @Override
    public boolean runCommand(String[] args) {
        Validate.isTrue(started, "cluster must be started before it can run commands...");

        if (commandExecutor.canExecute(args)) {
            commandExecutor.execute(args);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void startup() {
        this.startup(null, null);
    }

    @Override
    public void startup(Runnable runnable) {
        this.startup(null, runnable);
    }

    @Override
    public void startup(String[] args) {
        this.startup(args, null);
    }

    @Override
    public void startup(String[] args, Runnable runnable) {
        Validate.isTrue(!started, "cluster can not be started because startup Method was already called before.");

        for (IService value : this.serviceHashMap.values()) {
            setupService(value);
        }

        this.started = true;

        final ConfigService configService = this.get(ConfigService.class);
        final ConnectionService connectionService = this.get(ConnectionService.class);

        if (connectionService != null && configService != null) {
            commandExecutor.setupCommands(configService.getConfigManager(), connectionService.getIConnectionManager());
        }

        boolean run = true;

        if (args != null && runCommand(args)) {
            run = false;
        }

        if (runnable != null && run) {
            runnable.run();
        }
    }

    @Override
    public void addServices(final IService... IServices) {
        addServices(false, IServices);
    }

    @Override
    public void addServices(boolean initiate, final IService... IServices) {
        for (IService iService : IServices) {
            if (this.serviceHashMap.containsKey(iService.getName())) {
                System.out.println("Service already initialized: " + iService.getName());
                continue;
            }

            this.serviceHashMap.put(iService.getName(), iService);
            if (initiate) {
                setupService(iService);
            }
        }
    }

    private void setupService(IService iService) {
        iService.init();
        iService.registerCommand(this.commandExecutor);
    }

    @Override
    public void shutdownCluster() {
        for (IService value : serviceHashMap.values()) {
            value.disable();
        }
        serviceHashMap.clear();
    }

    @Override
    public <T extends IService> T get(Class<T> clazz) {
        return (T) serviceHashMap.getOrDefault(clazz.getName(), null);
    }
}
