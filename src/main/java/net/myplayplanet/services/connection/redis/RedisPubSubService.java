package net.myplayplanet.services.connection.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.lettuce.core.RedisFuture;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.provider.RedisManager;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class RedisPubSubService {

    private static final ObjectMapper MAPPER;

    private final RedisManager redisManager;

    private final Map<String, ThrowingConsumer<byte[]>> subscriptionConsumers = new ConcurrentHashMap<>();

    public RedisPubSubService(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    static {
        MAPPER = new ObjectMapper();
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public void pub(String channel, Object value) {
        try {
            byte[] json = toJson(value);
            redisManager.getByteConnection().async().publish(channel.getBytes(StandardCharsets.UTF_8), json);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private byte[] toJson(Object value) throws JsonProcessingException {
        if (value instanceof String) {
            return value.toString().getBytes(StandardCharsets.UTF_8);
        } else if (value instanceof byte[]) {
            return (byte[]) value;
        }


        return MAPPER.writeValueAsBytes(value);
    }

    public <T> void sub(String channel, Class<T> clazz, ThrowingConsumer<T> action) {
        synchronized (subscriptionConsumers) {
            if (subscriptionConsumers.isEmpty()) {
                redisManager.getBytePubSubConnection().addListener(new RedisByteArrayListener(this));
            }
            redisManager.getBytePubSubConnection().async().subscribe(channel.getBytes(StandardCharsets.UTF_8));

            ThrowingConsumer<byte[]> subscriptionConsumer = (byte[] json) -> {
                T value = MAPPER.readValue(json, clazz);
                action.accept(value);
            };
            subscriptionConsumers.put(channel, subscriptionConsumer);
        }
    }

    public void execute(byte[] channel, byte[] message) {
        ThrowingConsumer<byte[]> consumer = subscriptionConsumers.get(new String(channel, StandardCharsets.UTF_8));
        if (consumer == null) {
            return;
        }

        try {
            consumer.accept(message);
        } catch (Exception ex) {
            log.error("Could not consume message from channel {}", new String(channel, StandardCharsets.UTF_8));
        }
    }
}
