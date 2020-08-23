package net.myplayplanet.services.connection.provider;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import net.myplayplanet.services.connection.AbstractConnectionManager;
import net.myplayplanet.services.connection.ConnectionSetting;

import java.util.concurrent.ExecutionException;

public class RedisClusterManager extends AbstractConnectionManager {
    @Getter
    private StatefulRedisClusterConnection<byte[], byte[]> byteConnection;
    @Getter
    private StatefulRedisClusterPubSubConnection<byte[], byte[]> bytePubSubConnection;
    @Getter
    private StatefulRedisClusterConnection<String, String> stringConnection;
    @Getter
    private StatefulRedisClusterPubSubConnection<String, String> stringPubSubConnection;


    public RedisClusterManager(ConnectionSetting settings) {
        super(settings);
    }

    @Override
    public void createConnection() {

        String hostname = this.getSetting().getHostname();
        Integer port = this.getSetting().getPort();
        System.out.println("creating RedisCluster Connection with hostname  " + hostname + " port " + port + ".");

        RedisURI redisUri;
        if (this.getSetting().getPassword() == null) {
            redisUri = RedisURI.Builder.redis(hostname).withPort(port).build();
        } else {
            redisUri = RedisURI.Builder.redis(hostname).withPort(port).withPassword(this.getSetting().getPassword()).build();
        }

        RedisClusterClient redisClient = RedisClusterClient.create(redisUri);

        this.byteConnection = redisClient.connect(new ByteArrayCodec());
        this.bytePubSubConnection = redisClient.connectPubSub(new ByteArrayCodec());
        this.stringConnection = redisClient.connect(new StringCodec());
        this.stringPubSubConnection = redisClient.connectPubSub(new StringCodec());

        try {
            System.out.println("Testing Cluster Byte Connection: " + this.byteConnection.async().ping().get());
            System.out.println("Testing Cluster BytePubSub Connection: " + this.bytePubSubConnection.async().ping().get());
            System.out.println("Testing Cluster String Connection: " + this.stringConnection.async().ping().get());
            System.out.println("Testing Cluster StringPubSub Connection: " + this.stringPubSubConnection.async().ping().get());
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("creating RedisCluster Connection failed...");
            e.printStackTrace();
            return;
        }
        System.out.println("created RedisCluster Connection!");
    }

    @Override
    public void init() {

    }
}
