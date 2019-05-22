package net.myplayplanet.service.junit.cache;

import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.config.ConfigService;
import net.myplayplanet.services.logger.LoggerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;

public class CacheTests {
    Cache<String, TestObject> sut;

    @BeforeAll
    public static void beforeAll() {
        ServiceCluster.setDebug(true);
        ServiceCluster.startupCluster(new File("home"));
    }

    @BeforeEach
    public void beforeEach() {
        sut = new Cache<>("sut", s -> new TestObject(s, UUID.randomUUID()));
    }


    @Test
    public void simple_auto_add_to_local_cache_test() {
        TestObject obj = sut.get("test");

        Assertions.assertEquals(obj, sut.get("test"));
    }
}
