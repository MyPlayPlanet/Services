package net.myplayplanet.services.connection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.IService;
import net.myplayplanet.services.cluster.BaseServiceCluster;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.config.provider.IConfigManager;
import net.myplayplanet.services.config.provider.IResourceProvider;
import net.myplayplanet.services.connection.dbversion.UpdateCommand;
import net.myplayplanet.services.connection.exceptions.ConnectionTypeNotFoundException;
import net.myplayplanet.services.connection.exceptions.InvalidConnectionSettingFileException;
import net.myplayplanet.services.internal.CommandExecutor;

import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.util.Properties;

@Slf4j
public class ConnectionService implements IService {
    @Getter
    private ConnectionManager connectionManager;
    private IResourceProvider resourceProvider;
    private BaseServiceCluster baseServiceCluster;

    public ConnectionService(BaseServiceCluster baseServiceCluster, IResourceProvider resourceProvider) {
        this.baseServiceCluster = baseServiceCluster;
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void init() {
        System.out.println("starting ConnectionService");
        IConfigManager configManager = baseServiceCluster.get(ConfigService.class).getConfigManager();

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
            connectionManager = new ConnectionManager(configManager);
        } catch (NoSuchMethodException | ConnectionTypeNotFoundException | IllegalAccessException | InstantiationException | InvalidConnectionSettingFileException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerCommand(CommandExecutor executor) {
        executor.registerCommand(new UpdateCommand(this.resourceProvider));
    }
}