package net.myplayplanet.service.junit.test.cache;

import net.myplayplanet.service.junit.utils.ServiceInitializer;
import net.myplayplanet.service.junit.utils.TestObject;
import net.myplayplanet.services.cache.AbstractSaveProvider;
import net.myplayplanet.services.cache.advanced.MapCache;
import net.myplayplanet.services.cache.providers.MockProvider;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MapCacheTest {
    @BeforeAll
    public static void beforeAll() {
        ServiceInitializer.beforeAll();
    }
    @AfterAll
    public static void afterAll() {
        ServiceInitializer.afterAll();
    }

    HashMap<String, TestObject> sqlMap = new HashMap<>();
    MapCache<String, TestObject> mapCache;
    TestObject testObject1 = new TestObject("test", UUID.randomUUID());
    TestObject testObject2 = new TestObject("test2", UUID.randomUUID());

    @BeforeEach
    public void beforeEach() {
        sqlMap.put("test",testObject1);
        sqlMap.put("test2", testObject2);

        mapCache = new MapCache<>("test-cache", s -> {
            TestObject testObject = sqlMap.get(s);

            if (testObject != null) {
                return testObject;
            }

            return new TestObject(s, UUID.randomUUID());
        }, new AbstractSaveProvider<String, TestObject>() {
            @Override
            public boolean save(String key, TestObject value) {
                sqlMap.put(key, value);
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
    }

    @Test
    public void simple_check_if_value_is_writen_to_redis() {
        HashMap<String, TestObject> redisMap = MockProvider.getTotalMap().get("test-cache");

        Assertions.assertEquals(2,redisMap.size());

        TestObject test1 = redisMap.get("test");
        Assertions.assertEquals("test", test1.getString());
        Assertions.assertEquals(testObject1.getUuid(), test1.getUuid());

        TestObject test2 = redisMap.get("test2");
        Assertions.assertEquals("test2", test2.getString());
        Assertions.assertEquals(testObject2.getUuid(), test2.getUuid());
    }


    @Test
    public void simple_test_add_object_and_put_to_sql_test() {
        TestObject obj = mapCache.get("baum");

        Assertions.assertEquals(2,sqlMap.size());
        Assertions.assertEquals("baum", mapCache.get("baum").getString());
        Assertions.assertEquals(obj.getUuid(), mapCache.get("baum").getUuid());

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Assertions.assertEquals(3,sqlMap.size());

        Assertions.assertEquals("baum", sqlMap.get("baum").getString());
        Assertions.assertEquals(obj.getUuid(), sqlMap.get("baum").getUuid());
    }

    @Test
    public void simple_get_list_test_1() {
        ArrayList<TestObject> l1 = new ArrayList<>(mapCache.getList());
        Assertions.assertEquals(2, l1.size());

        Assertions.assertEquals("test2", l1.get(0).getString());
        Assertions.assertEquals(testObject2.getUuid(), l1.get(0).getUuid());

        Assertions.assertEquals("test", l1.get(1).getString());
        Assertions.assertEquals(testObject1.getUuid(), l1.get(1).getUuid());

        TestObject obj = mapCache.get("baum");

        ArrayList<TestObject> l2 = new ArrayList<>(mapCache.getList());
        Assertions.assertEquals(3, l2.size());

        Assertions.assertEquals("baum", l2.get(2).getString());
        Assertions.assertEquals(obj.getUuid(), l2.get(2).getUuid());

    }
}
