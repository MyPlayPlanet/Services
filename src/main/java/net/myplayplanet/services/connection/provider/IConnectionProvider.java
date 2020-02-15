package net.myplayplanet.services.connection.provider;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.sql.Connection;

public interface IConnectionProvider {
    StatefulRedisConnection<byte[], byte[]> getByteConnection();

    StatefulRedisPubSubConnection<byte[], byte[]> getBytePubSubConnection();

    StatefulRedisConnection<String, String> getStringConnection();

    StatefulRedisPubSubConnection<String, String> getStringPubSubConnection();

    Connection getMySQLConnection();
}
