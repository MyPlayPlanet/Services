package net.myplayplanet.services.connection;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.connection.provider.IConnectionProvider;
import net.myplayplanet.services.connection.provider.MockConnectionProvider;
import net.myplayplanet.services.connection.provider.SqlRedisConnectionProvider;

import java.sql.Connection;

@Slf4j
public class ConnectionManager {
    private IConnectionProvider provider;

    protected ConnectionManager(ConnectionSettings redisSetting, ConnectionSettings mysqlSetting, boolean debug) {
        assert redisSetting != null : "Redis Setting can not be null";
        assert mysqlSetting != null : "SQL Setting can not be null";
        System.out.println("creating ConnectionManager.");
        if (debug) {
            provider = new MockConnectionProvider();
        } else {
            provider = new SqlRedisConnectionProvider(redisSetting, mysqlSetting);
        }
    }

    public StatefulRedisConnection<byte[], byte[]> getByteConnection() {
        return provider.getByteConnection();
    }

    public StatefulRedisPubSubConnection<byte[], byte[]> getBytePubSubConnection() {
        return provider.getBytePubSubConnection();
    }

    public StatefulRedisConnection<String, String> getStringConnection() {
        return provider.getStringConnection();
    }

    public StatefulRedisPubSubConnection<String, String> getStringPubSubConnection() {
        return provider.getStringPubSubConnection();
    }

    public Connection getMySQLConnection() {
        return this.provider.getMySQLConnection();
    }
}
