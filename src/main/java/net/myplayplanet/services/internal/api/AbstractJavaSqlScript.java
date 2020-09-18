package net.myplayplanet.services.internal.api;

import java.sql.Connection;

public abstract class AbstractJavaSqlScript {
    public abstract void onStart(Connection connection);

    public abstract void onFinish(Connection connection);

    public abstract boolean dropDataBase();
}
