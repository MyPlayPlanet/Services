package net.myplayplanet.services.auth;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.logger.Log;

import java.util.UUID;

@Slf4j
public class AuthenticationService extends AbstractService {

    AuthenticationManager authenticationManager;
    @Override
    public void init() {
        Log.getLog(log).info("Starting {service}...", "AuthenticationService");
        authenticationManager = new AuthenticationManager();
        this.authenticate();
    }

    public void authenticate() {
        UUID username = UUID.randomUUID();
        UUID password = UUID.randomUUID();

        authenticationManager.updateCredentials(username, password.toString());
        authenticationManager.setSecretToken(authenticationManager.requestToken(username, password.toString()));
    }


    @Override
    public void disable() {
        Log.getLog(log).info("Shutting down {service}...", "AuthenticationService");
    }
}
