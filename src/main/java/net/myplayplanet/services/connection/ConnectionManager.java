package net.myplayplanet.services.connection;

import net.myplayplanet.services.config.api.IConfigManager;
import net.myplayplanet.services.connection.api.IConnectionManager;
import net.myplayplanet.services.connection.exceptions.ConnectionTypeNotFoundException;
import net.myplayplanet.services.connection.exceptions.InvalidConnectionSettingFileException;
import net.myplayplanet.services.connection.provider.MySqlManager;
import net.myplayplanet.services.connection.provider.RedisClusterManager;
import net.myplayplanet.services.connection.provider.RedisManager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager implements IConnectionManager {
    private final Map<String, Map<String, AbstractConnectionManager>> managers;
    private final Map<String, Class<? extends AbstractConnectionManager>> configManagerTypesStringClass;
    private final Map<Class<?>, String> configManagerTypesClassString;
    private final ConnectionConfigManager iConfigManager;

    public ConnectionManager(IConfigManager iConfigManager) throws NoSuchMethodException, ConnectionTypeNotFoundException, IllegalAccessException, InstantiationException, InvalidConnectionSettingFileException, InvocationTargetException {
        this.managers = new HashMap<>();
        this.configManagerTypesStringClass = new HashMap<>();
        this.configManagerTypesClassString = new HashMap<>();
        this.iConfigManager = new ConnectionConfigManager(iConfigManager);
        this.loadClasses();
        this.load();
    }

    private void loadClasses() {
        addManagerType(MySqlManager.class);
        addManagerType(RedisClusterManager.class);
        addManagerType(RedisManager.class);
    }

    /**
     * this method adds a new Manager Type, example for a Manager type is for example {@link net.myplayplanet.services.connection.provider.MySqlManager}
     */
    @Override
    public void addManagerType(Class<? extends AbstractConnectionManager> clazz) {
        String managerName = clazz.getSimpleName().replace("Manager", "").toLowerCase();
        configManagerTypesStringClass.put(managerName, clazz);
        configManagerTypesClassString.put(clazz, managerName);
        managers.put(managerName, new HashMap<>());
    }

    @Override
    public void setupConnectionInstance(String fileName, ConnectionSetting setting) throws InvalidConnectionSettingFileException, ConnectionTypeNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (fileName.startsWith("example")) {
            System.out.println("skipped example properties file.");
            return;
        }

        if (!fileName.contains("-")) {
            throw new InvalidConnectionSettingFileException("invalid filename \" " + fileName + "\". Correct name pattern: \"<type>-[instance name]-settings.properties\"");
        }

        String[] split = fileName.split("-");
        String type = split[0].toLowerCase();
        String instanceName = split[1].contains("settings.properties") || split[1].trim().equalsIgnoreCase("") ? "default" : split[1];
        if (!configManagerTypesStringClass.containsKey(type)) {
            throw new ConnectionTypeNotFoundException("type with name \"" + type + "\" not found.");
        }

        System.out.println(
                "start creating connectionManager with instanceName '" + instanceName + "' and type ''" + type + "' for hostname '" + setting.hostname + "'...");
        Map<String, AbstractConnectionManager> stringAbstractConnectionManagerHashMap = managers.get(type);
        AbstractConnectionManager abstractConnectionManager = configManagerTypesStringClass.get(type).getConstructor(ConnectionSetting.class).newInstance(setting);

        stringAbstractConnectionManagerHashMap.put(instanceName, abstractConnectionManager);
        System.out.println("added connectionManager of type " + type + " with instance name " + instanceName);
    }

    private void load() throws InvalidConnectionSettingFileException, ConnectionTypeNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        HashMap<String, ConnectionSetting> connectionSettings = this.iConfigManager.getConnectionSettings();
        for (String key : connectionSettings.keySet()) {
            setupConnectionInstance(key, connectionSettings.get(key));
        }
    }

    @Override
    public <T extends AbstractConnectionManager> T get(Class<T> clazz, String instance) {
        if (!configManagerTypesClassString.containsKey(clazz)) {
            return null;
        }

        return (T) managers.get(configManagerTypesClassString.get(clazz)).get(instance);
    }

    @Override
    public <T extends AbstractConnectionManager> T get(Class<T> clazz) {
        return get(clazz, "default");
    }
}
