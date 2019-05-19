import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.ping.PingProvider;
import net.myplayplanet.services.ping.PingService;
import net.myplayplanet.services.ping.connection.Connection;
import net.myplayplanet.services.ping.events.ConnectionLostEvent;

import java.io.File;

public class exanokl {

    public static void main(String[] args) {
        ServiceCluster.startupCluster(new File("+"));
        ServiceCluster.addServices(true, new PingService("test_1"));

        PingProvider.getInstance().registerOutgoingChannel("test_2", 15);
        PingProvider.getInstance().registerEvent(new Event());

    }

}

class Event extends ConnectionLostEvent {
    @Override
    public void onConnectionLost(Connection connection) {
        System.out.println("Lost Connection to " + connection.getChannel());
    }
}