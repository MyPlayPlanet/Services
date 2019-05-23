package net.myplayplanet.services.ping.events;

import net.myplayplanet.services.ping.connection.Connection;

public abstract class ConnectionLostEvent {

    public abstract void onConnectionLost(Connection connection);

}
