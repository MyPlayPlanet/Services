import net.myplayplanet.services.ServiceCluster;

import java.io.File;

public class JustAMainClass {
    public static void main(String[] args) {
        ServiceCluster.setDebug(true);
        ServiceCluster.startupCluster(new File("home"));
    }
}
