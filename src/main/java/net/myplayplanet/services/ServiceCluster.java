package net.myplayplanet.services;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.provider.IConfigManager;
import net.myplayplanet.services.config.provider.IResourceProvider;
import net.myplayplanet.services.config.provider.config.FileConfigManager;
import net.myplayplanet.services.config.provider.config.MockConfigManager;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.schedule.ScheduleService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
public class ServiceCluster {
    private final ArrayList<AbstractService> IServiceList;
    private final IResourceProvider resourceProvider;

    public ServiceCluster(IResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
        IServiceList = new ArrayList<>();
    }

    public ServiceCluster() {
        this(IResourceProvider.getResourceProvider());
    }


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

    public void startupCluster(InputStream resourceStream) throws IOException {
        Properties properties = new Properties();
        properties.load(resourceStream);
        resourceStream.close();

        String configPath = properties.getProperty("mpp.basic.config-path");
        File configFile = new File(configPath);
        boolean debug = Boolean.parseBoolean(properties.getProperty("mpp.basic.debug"));

        this.startupCluster(configFile, debug);
    }

    public void startupCluster(String resourceFileName) throws IOException {
        try (InputStream inputStream = this.resourceProvider.getResourceFile(resourceFileName)) {
            this.startupCluster(inputStream);
        }
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
