package net.myplayplanet.service.junit.cache;

import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.service.junit.ServiceInitializer;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.logger.Log;
import org.junit.jupiter.api.*;

import java.util.UUID;
@Slf4j
public class SimpleCacheTests {
    Cache<String, TestObject> sut;

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
        sut = new Cache<>("sut", s -> new TestObject(s, UUID.randomUUID()));
    }

    @Test
    public void simple_test_1() {
        TestObject obj = sut.get("test");
        Assertions.assertEquals("test", obj.string);
        Assertions.assertEquals(obj.uuid, obj.uuid);
    }

    @Test
    public void simple_test_2() {
        //arrange
        TestObject obj = sut.get("test");
        Assertions.assertEquals(obj, sut.get("test"));
        sut.clearCache();

        //act
        TestObject newTestObj = sut.get("test");

        //assert
        Assertions.assertEquals("test", newTestObj.string);
        Assertions.assertNotEquals(obj.uuid, newTestObj.uuid);
    }


    @Test
    public void simple_test_3() {
        //arrange
        TestObject obj = sut.get("test");
        Assertions.assertEquals(obj, sut.get("test"));
        sut.clearLocalCache();

        //act
        TestObject newTestObj = sut.get("test");

        //assert
        Assertions.assertEquals("test", newTestObj.string);
        Assertions.assertEquals(obj.uuid, newTestObj.uuid);
    }
}
