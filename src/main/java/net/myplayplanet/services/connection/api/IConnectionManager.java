package net.myplayplanet.services.connection.api;

import net.myplayplanet.services.connection.AbstractConnectionManager;
import net.myplayplanet.services.connection.ConnectionSetting;
import net.myplayplanet.services.connection.exceptions.ConnectionTypeNotFoundException;
import net.myplayplanet.services.connection.exceptions.InvalidConnectionSettingFileException;

import java.lang.reflect.InvocationTargetException;

public interface IConnectionManager {
    void addManagerType(Class<? extends AbstractConnectionManager> clazz);

    void setupConnectionInstance(String fileName, ConnectionSetting setting) throws InvalidConnectionSettingFileException, ConnectionTypeNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;

    <T extends AbstractConnectionManager> T get(Class<T> clazz, String instance);

    <T extends AbstractConnectionManager> T get(Class<T> clazz);
}
