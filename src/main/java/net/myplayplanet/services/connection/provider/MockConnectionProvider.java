package net.myplayplanet.services.connection.provider;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.sql.Connection;

public class MockConnectionProvider implements IConnectionProvider{
    @Override
    public StatefulRedisConnection<byte[], byte[]> getByteConnection() {
        return null;
    }

    @Override
    public StatefulRedisPubSubConnection<byte[], byte[]> getBytePubSubConnection() {
        return null;
    }

    @Override
    public StatefulRedisConnection<String, String> getStringConnection() {
        return null;
    }

    @Override
    public StatefulRedisPubSubConnection<String, String> getStringPubSubConnection() {
        return null;
    }

    @Override
    public Connection getMySQLConnection() {
        return null;
    }
}
