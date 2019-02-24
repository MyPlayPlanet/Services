import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.config.ConfigService;

import java.io.File;

public class Example {

    public static void main(String[] args) {
        ServiceCluster.get(ConfigService.class).setPath(new File(""));
        ServiceCluster.init();
    }

}
