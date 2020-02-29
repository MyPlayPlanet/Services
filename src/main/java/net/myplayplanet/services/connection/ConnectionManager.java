package net.myplayplanet.services.connection;

import net.myplayplanet.services.config.provider.IConfigManager;
import net.myplayplanet.services.connection.exceptions.ConnectionTypeNotFoundException;
import net.myplayplanet.services.connection.exceptions.InvalidConnectionSettingFileException;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

public class ConnectionManager {
    private HashMap<String, HashMap<String, AbstractConnectionManager>> managers;
    private HashMap<String, Class<? extends AbstractConnectionManager>> configManagerTypesStringClass;
    private HashMap<Class<?>, String> configManagerTypesClassString;
    private ConnectionConfigManager iConfigManager;

    public ConnectionManager(IConfigManager iConfigManager) throws NoSuchMethodException, ConnectionTypeNotFoundException, IllegalAccessException, InstantiationException, InvalidConnectionSettingFileException, InvocationTargetException {
        this.managers = new HashMap<>();
        this.configManagerTypesStringClass = new HashMap<>();
        this.configManagerTypesClassString = new HashMap<>();
        this.iConfigManager = new ConnectionConfigManager(iConfigManager);
        this.loadClasses();
        this.load();
    }

    private void loadClasses() {
        Reflections reflections = new Reflections("net.myplayplanet.services.connection.provider");

        Set<Class<? extends AbstractConnectionManager>> allClasses =
                reflections.getSubTypesOf(AbstractConnectionManager.class);

        for (Class<? extends AbstractConnectionManager> aClass : allClasses) {
            addManagerType(aClass);
        }
    }

    public void addManagerType(Class<? extends AbstractConnectionManager> clazz) {
        String managerName = clazz.getSimpleName().replace("Manager", "").toLowerCase();
        configManagerTypesStringClass.put(managerName, clazz);
        configManagerTypesClassString.put(clazz, managerName);
        managers.put(managerName, new HashMap<>());
    }

    private void load() throws InvalidConnectionSettingFileException, ConnectionTypeNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        HashMap<String, ConnectionSetting> connectionSettings = this.iConfigManager.getConnectionSettings();
        for (String key : connectionSettings.keySet()) {

            if (key.startsWith("example")) {
                System.out.println("skipped example properties file.");
                continue;
            }

            ConnectionSetting setting = connectionSettings.get(key);
            if (!key.contains("-")) {
                throw new InvalidConnectionSettingFileException("invalid filename \" " + key +"\". Correct name pattern: \"<type>-[instance name]-settings.properties\"");
            }

            String[] split = key.split("-");
            String type = split[0].toLowerCase();
            String instanceName = split[1].contains("settings.properties") || split[1].trim().equalsIgnoreCase("")? "default" :  split[1];
            if (!configManagerTypesStringClass.containsKey(type)) {
                throw new ConnectionTypeNotFoundException("type with name \"" + type + "\" not found.");
            }

            System.out.println(
                    "start creating connectionManager with instanceName '" + instanceName + "' and type ''" + type + "' for hostname '" + setting.hostname + "'...");
            HashMap<String, AbstractConnectionManager> stringAbstractConnectionManagerHashMap = managers.get(type);
            AbstractConnectionManager abstractConnectionManager = configManagerTypesStringClass.get(type).getConstructor(ConnectionSetting.class).newInstance(setting);

            stringAbstractConnectionManagerHashMap.put(instanceName, abstractConnectionManager);
            System.out.println("added connectionManager.");
        }
    }

    public <T extends AbstractConnectionManager> T get(Class<T> clazz, String instance) {
        if (!configManagerTypesClassString.containsKey(clazz)) {
            return null;
        }

        return (T) managers.get(configManagerTypesClassString.get(clazz)).get(instance);
    }

    public <T extends AbstractConnectionManager> T get(Class<T> clazz) {
        return get(clazz, "default");
    }
}
