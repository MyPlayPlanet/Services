package net.myplayplanet.services.connection;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.connection.exceptions.ConnectionTypeNotFoundException;
import net.myplayplanet.services.connection.exceptions.InvalidConnectionSettingFileException;

import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.util.Properties;

@Slf4j
public class ConnectionService extends AbstractService {
    private boolean debug;
    @Getter
    private ConnectionManager loader;

    public ConnectionService(ServiceCluster cluster, boolean debug) {
        super(cluster);
        this.debug = debug;
    }

    @Override
    public void init() {
        System.out.println("starting ConnectionService");

        ConfigService service = this.getCluster().get(ConfigService.class);

        try {
            if (service.getConfigManager().getAllFilesInDirectory(
                    s -> s.toLowerCase().endsWith("settings.properties")
            ).length <= 0) {
                Properties example = new Properties();
                example.setProperty("hostname", Inet4Address.getLocalHost().getHostAddress());
                example.setProperty("database", "database");
                example.setProperty("port", "6379");
                example.setProperty("password", "foobared");
                example.setProperty("username", "username");

                if (service.getConfigManager().createSettingWithProperties("example-settings", example)) {
                    System.out.println("created setting example-settings");
                } else {
                    System.out.println("cloud not create example-settings");
                }
            }
        } catch (Exception e) {
            System.out.println("error setting properties: " + e.getMessage());
        }

        try {
            loader = new ConnectionManager(service.getConfigManager());
        } catch (NoSuchMethodException | ConnectionTypeNotFoundException | IllegalAccessException | InstantiationException | InvalidConnectionSettingFileException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}