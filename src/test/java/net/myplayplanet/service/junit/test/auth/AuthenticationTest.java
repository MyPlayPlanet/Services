package net.myplayplanet.service.junit.test.auth;

import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.auth.AuthenticationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class AuthenticationTest {

    @Test
    public void auth() {
        ServiceCluster.startupCluster(new File("MyPlayPlanet-Services"));
        Assertions.assertNotEquals(AuthenticationManager.getInstance().getSecretToken(), "");
    }

}
