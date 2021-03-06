package net.myplayplanet.services.connection.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.provider.RedisManager;
import net.myplayplanet.services.rest.ObjectMapperCreator;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RedisPubSubService {

    private final RedisManager redisManager;

    private final Map<String, ThrowingConsumer<byte[]>> subscriptionConsumers = new ConcurrentHashMap<>();

    public RedisPubSubService(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    /**
     * Publish a message asynchronously as json on a given channel.
     *
     * @param channel the channel the message should be published on
     * @param message The message that should be published
     */
    public void pub(String channel, Object message) {
        try {
            byte[] json = toJson(message);
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


        return ObjectMapperCreator.INSTANCE.writeValueAsBytes(value);
    }

    /**
     * Subscribe for message asynchronously
     * @param channel channel that should be subscribed on
     * @param clazz the message clazz
     * @param action consumer for the message
     * @param <T> type of the message
     */
    public <T> void sub(String channel, Class<T> clazz, ThrowingConsumer<T> action) {
        synchronized (subscriptionConsumers) {
            if (subscriptionConsumers.isEmpty()) {
                redisManager.getBytePubSubConnection().addListener(new RedisByteArrayListener(this));
            }
            redisManager.getBytePubSubConnection().async().subscribe(channel.getBytes(StandardCharsets.UTF_8));

            ThrowingConsumer<byte[]> subscriptionConsumer = (byte[] json) -> {
                T value;
                if (!clazz.isAssignableFrom(byte[].class)) {
                    value = ObjectMapperCreator.INSTANCE.readValue(json, clazz);
                } else {
                    value = (T) json;
                }
                action.accept(value);
            };
            subscriptionConsumers.put(channel, subscriptionConsumer);
        }
    }

    void execute(byte[] channel, byte[] message) {
        ThrowingConsumer<byte[]> consumer = subscriptionConsumers.get(new String(channel, StandardCharsets.UTF_8));
        if (consumer == null) {
            return;
        }

        try {
            consumer.accept(message);
        } catch (Exception ex) {
            log.error("Could not consume message from channel {}", new String(channel, StandardCharsets.UTF_8), ex);
        }
    }
}
