package net.myplayplanet.services;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.cache.CachingService;
import net.myplayplanet.services.checker.CheckService;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.connection.ConnectionService;
import net.myplayplanet.services.exeption.AlreadyInitializedException;
import net.myplayplanet.services.logger.Log;
import net.myplayplanet.services.logger.LoggerService;
import net.myplayplanet.services.schedule.ScheduleService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ServiceCluster {
    private static ArrayList<AbstractService> IServiceList = new ArrayList<>();

    public static void addServices(boolean initiate, final AbstractService... IServices) {
        List<AbstractService> services = Arrays.asList(IServices);
        services.forEach(service -> {
            if (validate(IServiceList.stream().anyMatch(abstractService1 -> abstractService1.getClass() == service.getClass()), "Already initialized")) {
                services.remove(service);
            }
        });

        IServiceList.addAll(services);
        if (initiate) {
            services.forEach(AbstractService::init);
        }
    }

    public static void addServices(final AbstractService... IServices) {
        addServices(false, IServices);
    }

    public static void startupCluster(File configPath) {
        addServices(true, new LoggerService());
        addServices(true, new ConfigService(configPath));
        addServices(true, new ConnectionService());
        addServices(true, new CachingService());
        addServices(true, new ScheduleService());
        addServices(true, new CheckService());
    }

    public static void shutdownCluster() {
        IServiceList.forEach(AbstractService::disable);
    }

    /**
     * @param type Class of the {@link AbstractService}, which ist requested
     * @param <T>  Type of the {@link AbstractService} to cast it automatically to the wanted {@link AbstractService}
     * @return The service instance filtered by {@param type}
     */
    public static <T extends AbstractService> T get(final Class<T> type) {
        return type.cast(IServiceList.stream().filter(IService -> IService.getClass() == type).findFirst().get());
    }

    /**
     * @param expression which should be true
     * @param message the error message which will be thrown
     * @return expression
     */

    public static boolean validate(boolean expression, String message){
        if(expression){
            Log.getLog(log).error( new AlreadyInitializedException(message), message);
            return true;
        }
        return false;
    }
}
