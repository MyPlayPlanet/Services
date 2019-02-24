import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.logger.LoggerService;

import java.io.File;

public class Example {

    public static void main(String[] args) {
        ServiceCluster.initiateCluster(new File("D:\\temp\\mpp"));

        ServiceCluster.addServices(false, new LoggerService());
        ServiceCluster.get(LoggerService.class).init();


    }

}
