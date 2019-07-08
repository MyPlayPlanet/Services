package net.myplayplanet.service.junit.test.cache;

import net.myplayplanet.service.junit.utils.ServiceInitializer;
import net.myplayplanet.service.junit.utils.TestObject;
import net.myplayplanet.services.cache.AbstractSaveProvider;
import net.myplayplanet.services.cache.advanced.MapCache;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SimpleMapCacheTests {

    @BeforeAll
    public static void beforeAll() {
        ServiceInitializer.beforeAll();
    }
    @AfterAll
    public static void afterAll() {
        ServiceInitializer.afterAll();
    }

    int saveAmount = 0;

    @Test
    public void remove_entry_from_save_queue_after_saving() {
        HashMap<String, TestObject> sqlMap = new HashMap<>();

        MapCache<String, TestObject> mapCache = new MapCache<>("test-cache", s -> {
            TestObject testObject = sqlMap.get(s);

            if (testObject != null) {
                return testObject;
            }

            return new TestObject(s, UUID.randomUUID());
        }, new AbstractSaveProvider<String, TestObject>() {
            @Override
            public boolean save(String key, TestObject value) {
                sqlMap.put(key, value);
                saveAmount++;
                return true;
            }

            @Override
            public HashMap<String, TestObject> load() {
                return sqlMap;
            }

            @Override
            public TimeUnit getIntervalUnit() {
                return TimeUnit.SECONDS;
            }

            @Override

            public int getInterval() {
                return 1;
            }
        });

        mapCache.get("baum");


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(1, saveAmount);

    }
}
