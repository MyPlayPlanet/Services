
import net.myplayplanet.services.cache_new.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class CacheTests {
    Cache<String, UUID> sut;

    @BeforeEach
    public void beforeEach() {
        sut = new Cache<>("sut", s -> UUID.randomUUID());
    }


    @Test
    public void simpleTest() {

    }
}
