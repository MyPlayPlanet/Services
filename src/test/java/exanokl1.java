import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.logger.LoggerService;
import net.myplayplanet.services.ping.PingProvider;
import net.myplayplanet.services.ping.PingService;

import java.io.File;

public class exanokl1 {

    public static void main(String[] args) {
        ServiceCluster.startupCluster(new File("+"));
        ServiceCluster.addServices(true, new PingService("test_2"));

        PingProvider.getInstance().registerOutgoingChannel("test_1", 15);

    }

}
