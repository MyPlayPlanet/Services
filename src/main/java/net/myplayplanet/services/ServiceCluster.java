package net.myplayplanet.services;

import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.logger.LoggerService;

import java.util.ArrayList;
import java.util.Arrays;

public class ServiceCluster {

    private static ArrayList<AbstractService> IServiceList = new ArrayList<>();

    static {
        addServices(new LoggerService());
        addServices(new ConfigService());
        addServices(new ConnectionService());
    }

    /**
     * Add as many {@link AbstractService}s as you want to the {@link ServiceCluster#IServiceList}
     *
     * @param IServices {@link AbstractService}s you want to add
     */
    public static void addServices(final AbstractService... IServices) {
        IServiceList.addAll(Arrays.asList(IServices));
    }

    public static void init() {
        IServiceList.forEach(AbstractService::init);
    }

    /**
     * @param type Class of the {@link AbstractService}, which ist requested
     * @param <T>  Type of the {@link AbstractService} to cast it automatically to the wanted {@link AbstractService}
     * @return The service instance filtered by {@param type}
     */
    public static <T extends AbstractService> T get(final Class<T> type) {
        return type.cast(IServiceList.stream().filter(IService -> IService.getClass() == type).findFirst().get());
    }

}
