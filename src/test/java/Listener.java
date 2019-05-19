import net.myplayplanet.services.ping.connection.Connection;
import net.myplayplanet.services.ping.events.ConnectionLostEvent;

public class Listener extends ConnectionLostEvent {
    @Override
    public void onConnectionLost(Connection connection) {

    }
}
