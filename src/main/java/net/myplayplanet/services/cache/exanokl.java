package net.myplayplanet.services.cache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class exanokl extends ISQLCacheProvider<UUID> {

    public exanokl(String cacheName, TimeUnit intervalUnit, int interval) {
        super(cacheName, intervalUnit, interval);
    }

    @Override
    void update() {

    }
}
