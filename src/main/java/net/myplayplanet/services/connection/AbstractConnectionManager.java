package net.myplayplanet.services.connection;

import lombok.Getter;

public abstract class AbstractConnectionManager {

    @Getter
    private final ConnectionSetting setting;

    public AbstractConnectionManager(ConnectionSetting setting) {
        this.setting = setting;
        init();
        createConnection();
    }

    public abstract void createConnection();

    public abstract void init();
}
