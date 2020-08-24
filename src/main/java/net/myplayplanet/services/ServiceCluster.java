package net.myplayplanet.services;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.provider.IResourceProvider;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.internal.CommandExecutor;
import net.myplayplanet.services.schedule.ScheduleService;

import java.util.HashMap;

@Slf4j
public class ServiceCluster {
    private final HashMap<String, IService> serviceHashMap;
    private final CommandExecutor commandExecutor;

    public ServiceCluster(IResourceProvider resourceProvider) {
        serviceHashMap = new HashMap<>();
        commandExecutor = new CommandExecutor();

        addServices(new ConfigService(resourceProvider));
        addServices(true, new ConnectionService(this.get(ConfigService.class).getConfigManager()));
        addServices(true, new ScheduleService());

        commandExecutor.setupCommands(this.get(ConfigService.class).getConfigManager(), this.get(ConnectionService.class).getConnectionManager());
    }

    public ServiceCluster() {
        this(IResourceProvider.getResourceProvider());
    }

    public void run(String[] args, Runnable runnable) {
        if (commandExecutor.canExecute(args)) {
            commandExecutor.execute(args);
        } else {
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
