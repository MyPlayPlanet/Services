package net.myplayplanet.services.connection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.api.IService;
import net.myplayplanet.services.api.IServiceCluster;
import net.myplayplanet.services.cluster.JavaServiceCluster;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.api.IConfigManager;
import net.myplayplanet.services.config.api.IResourceProvider;
import net.myplayplanet.services.connection.dbversion.UpdateCommand;
import net.myplayplanet.services.connection.exceptions.ConnectionTypeNotFoundException;
import net.myplayplanet.services.connection.exceptions.InvalidConnectionSettingFileException;
import net.myplayplanet.services.internal.CommandExecutor;

import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.util.Properties;

@Slf4j
public class ConnectionService implements IService {
    private final IResourceProvider resourceProvider;
    private final IServiceCluster iServiceCluster;
    @Getter
    private net.myplayplanet.services.connection.api.IConnectionManager IConnectionManager;

    public ConnectionService(JavaServiceCluster iServiceCluster, IResourceProvider resourceProvider) {
        this.iServiceCluster = iServiceCluster;
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void init() {
        System.out.println("starting ConnectionService");
        IConfigManager configManager = iServiceCluster.get(ConfigService.class).getConfigManager();

        try {
            if (configManager.getAllFilesInDirectory(
                    s -> s.toLowerCase().endsWith("settings.properties")
            ).length <= 0) {
                Properties example = new Properties();
                example.setProperty("hostname", Inet4Address.getLocalHost().getHostAddress());
                example.setProperty("database", "database");
                example.setProperty("port", "6379");
                example.setProperty("password", "foobared");
                example.setProperty("username", "username");

                if (configManager.createSettingWithProperties("example-settings", example)) {
                    System.out.println("created setting example-settings");
                } else {
                    System.out.println("cloud not create example-settings");
                }
            }
        } catch (Exception e) {
            System.out.println("error setting properties: " + e.getMessage());
        }

        try {
            IConnectionManager = new ConnectionManager(configManager);
        } catch (NoSuchMethodException | ConnectionTypeNotFoundException | IllegalAccessException | InstantiationException | InvalidConnectionSettingFileException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerCommand(CommandExecutor executor) {
        executor.registerCommand(new UpdateCommand(this.resourceProvider));
    }
}