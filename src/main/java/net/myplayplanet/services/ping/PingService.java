package net.myplayplanet.services.ping;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.AbstractService;
import net.myplayplanet.services.logger.Log;

@Slf4j
public class PingService extends AbstractService {

    @Getter(AccessLevel.PRIVATE)
    private String channel;

    public PingService(String channel){
        this.channel = channel;
    }

    @Override
    public void init() {
        Log.getLog(log).info("Starting {service}...", "PingService");
        new PingProvider(this.getChannel());
    }

    @Override
    public void disable() {
        Log.getLog(log).info("Shutting down {service}...", "PingService");
    }

}
