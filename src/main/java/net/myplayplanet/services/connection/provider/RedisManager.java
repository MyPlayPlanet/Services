package net.myplayplanet.services.connection.provider;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.AbstractConnectionManager;
import net.myplayplanet.services.connection.ConnectionSetting;
import net.myplayplanet.services.connection.api.IDataProvider;
import net.myplayplanet.services.connection.api.KeyDataProvider;
import net.myplayplanet.services.connection.helper.SerializeHelper;
import net.myplayplanet.services.connection.redis.RedisPubSubService;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class RedisManager extends AbstractConnectionManager implements IDataProvider {
    @Getter
    private StatefulRedisConnection<byte[], byte[]> byteConnection;
    @Getter
    private StatefulRedisPubSubConnection<byte[], byte[]> bytePubSubConnection;
    @Getter
    private StatefulRedisConnection<String, String> stringConnection;
    @Getter
    private StatefulRedisPubSubConnection<String, String> stringPubSubConnection;

    @Getter
    private RedisPubSubService redisPubSubService;

    public RedisManager(ConnectionSetting settings) {
        super(settings);
    }

    @Override
    public void createConnection() {

        String hostname = this.getSetting().getHostname();
        Integer port = this.getSetting().getPort();
        log.info("creating Redis Connection with hostname {} port {}.", hostname, port);

        RedisURI redisUri;
        if (this.getSetting().getPassword() == null) {
            redisUri = RedisURI.Builder.redis(hostname).withPort(port).build();
        } else {
            redisUri = RedisURI.Builder.redis(hostname).withPort(port).withPassword(this.getSetting().getPassword()).build();
        }

        RedisClient redisClient = RedisClient.create(redisUri);

        this.byteConnection = redisClient.connect(new ByteArrayCodec());
        this.bytePubSubConnection = redisClient.connectPubSub(new ByteArrayCodec());
        this.stringConnection = redisClient.connect(new StringCodec());
        this.stringPubSubConnection = redisClient.connectPubSub(new StringCodec());
        this.redisPubSubService = new RedisPubSubService(this);

        try {
            log.info("Testing Byte Connection: {}", this.byteConnection.async().ping().get());
            log.info("Testing BytePubSub Connection: {}", this.bytePubSubConnection.async().ping().get());
            log.info("Testing String Connection: {}", this.stringConnection.async().ping().get());
            log.info("Testing StringPubSub Connection: {}", this.stringPubSubConnection.async().ping().get());
        } catch (InterruptedException | ExecutionException e) {
            log.error("creating Redis Connection failed...", e);
            return;
        }
        log.info("created Redis Connection!");


    }

    @Override
    public void init() {

    }

    @Override
    public KeyDataProvider withKey(String key) {
        return new KeyDataProvider(key, this);
    }

    @Override
    public <T extends Serializable> CompletableFuture<T> get(String key, String field) {
        return this.getByteConnection()
                .async()
                .hget(key.getBytes(), field.getBytes())
                .thenApplyAsync(SerializeHelper::<T>deserializeToType)
                .toCompletableFuture();
    }

    @Override
    public <T extends Serializable> CompletableFuture<Map<String, T>> getAll(String key) {
        return this.getByteConnection()
                .async()
                .hgetall(key.getBytes())
                .thenApplyAsync(map -> {
                    Map<String, T> res = new HashMap<>();
                    for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                        res.put(new String(entry.getKey(), StandardCharsets.UTF_8), SerializeHelper.deserializeToType(entry.getValue()));
                    }
                    return res;
                }).toCompletableFuture();
    }

    @Override
    public <T extends Serializable> CompletableFuture<List<T>> getAllValues(String key) {
        return this.getByteConnection()
                .async()
                .hgetall(key.getBytes())
                .thenApplyAsync(map -> {
                    List<T> result = new ArrayList<>();
                    for (byte[] value : map.values()) {
                        result.add(SerializeHelper.deserializeToType(value));
                    }
                    return result;
                }).toCompletableFuture();
    }

    @Override
    public <T extends Serializable> CompletableFuture<Boolean> set(String key, String field, T data) {
        return this.getByteConnection()
                .async()
                .hset(key.getBytes(), field.getBytes(), SerializationUtils.serialize(data))
                .toCompletableFuture();
    }
}
