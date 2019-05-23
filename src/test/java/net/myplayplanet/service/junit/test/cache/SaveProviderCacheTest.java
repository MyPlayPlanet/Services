package net.myplayplanet.service.junit.test.cache;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.service.junit.utils.ServiceInitializer;
import net.myplayplanet.service.junit.utils.TestObject;
import net.myplayplanet.services.cache.AbstractSaveProvider;
import net.myplayplanet.services.cache.Cache;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SaveProviderCacheTest {
    private HashMap<String, TestObject> sqlSave = new HashMap<>();

    @BeforeAll
    public static void beforeAll() {
        ServiceInitializer.beforeAll();
    }
    @AfterAll
    public static void afterAll() {
        ServiceInitializer.afterAll();
    }

    @BeforeEach
    public void beforeEach() {
        sqlSave.clear();
    }

    @Test
    public void simple_test_1() {
        Cache<String, TestObject> sut = new Cache<>("sut", s -> new TestObject(s, UUID.randomUUID()), new AbstractSaveProvider<String, TestObject>() {
            @Override
            public boolean save(String key, TestObject value) {
                sqlSave.put(key, value);
                return true;
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



        TestObject obj = sut.get("test");

        Assertions.assertEquals(0, sqlSave.size());

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(1, sqlSave.size());
        Assertions.assertEquals(obj, sqlSave.get("test"));
    }
    @Test
    public void simple_test_2() {
        UUID uuid = UUID.randomUUID();

        Cache<String, TestObject> sut = new Cache<>("sut", s -> new TestObject(s, UUID.randomUUID()), new AbstractSaveProvider<String, TestObject>() {
            @Override
            public boolean save(String key, TestObject value) {
                sqlSave.put(key, value);
                return true;
            }

            @Override
            public HashMap<String, TestObject> load() {
                HashMap<String, TestObject> map = new HashMap<>();

                map.put("test", new TestObject("test", uuid));

                return map;
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

        TestObject obj = sut.get("test");

        Assertions.assertEquals("test" ,obj.getString());
        Assertions.assertEquals(uuid ,obj.getUuid());
    }
}
