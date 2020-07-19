package net.myplayplanet.services;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.provider.FileConfigManager;
import net.myplayplanet.services.config.provider.IConfigManager;
import net.myplayplanet.services.config.provider.MockConfigManager;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.schedule.ScheduleService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ServiceCluster {
    private ArrayList<AbstractService> IServiceList = new ArrayList<>();

    public void addServices(boolean initiate, final AbstractService... IServices) {
        List<AbstractService> services = Arrays.asList(IServices);
        List<AbstractService> alreadyIntializedServices = new ArrayList<>();

        services.forEach(service -> {
            if (validate(IServiceList.stream().anyMatch(abstractService1 -> abstractService1.getClass() == service.getClass()), "Already initialized")) {
                System.out.println("Service already Intialized: " + service.getClass());
                alreadyIntializedServices.add(service);
            }
        });

        alreadyIntializedServices.forEach(services::remove);

        IServiceList.addAll(services);
        if (initiate) {
            services.forEach(AbstractService::init);
        }
    }

    public void addServices(final AbstractService... IServices) {
        addServices(false, IServices);
    }

    public void startupCluster(File configPath, boolean debug) {
        IConfigManager configManager = debug ? new MockConfigManager(configPath) : new FileConfigManager(configPath);


        addServices(true, new ConfigService(this, configManager));
        addServices(true, new ConnectionService(this, debug));
        addServices(true, new ScheduleService(this));
    }

    public void shutdownCluster() {
        IServiceList.forEach(AbstractService::disable);
        IServiceList.clear();
    }

    public <T extends AbstractService> T get(Class<T> clazz) {
        for (AbstractService abstractService : IServiceList) {
            if (abstractService.getClass() == clazz) {
                return (T) abstractService;
            }
        }
        return null;
    }

    public boolean validate(boolean expression, String message) {
        if (expression) {
            System.out.println("AlreadyInitializedException:" + message);
            return true;
        }
        return false;
    }
}
