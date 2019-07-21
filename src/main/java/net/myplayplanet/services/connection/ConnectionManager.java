package net.myplayplanet.services.connection;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.connection.provider.IConnectionProvider;
import net.myplayplanet.services.connection.provider.MockConnectionProvider;
import net.myplayplanet.services.connection.provider.SqlRedisConnectionProvider;
import net.myplayplanet.services.logger.Log;

import java.sql.Connection;

@Slf4j
public class ConnectionManager {
    @Getter
    private static ConnectionManager instance;
    private IConnectionProvider provider;

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

    protected ConnectionManager(ConnectionSettings redisSetting, ConnectionSettings mysqlSetting) {
        Log.getLog(log).info("creating ConnectionManager.");
        instance = this;
        if (ServiceCluster.isDebug()) {
            //provider = new SqlRedisConnectionProvider(redisSetting, mysqlSetting);
            provider = new MockConnectionProvider();
        }else {
            provider = new SqlRedisConnectionProvider(redisSetting, mysqlSetting);
        }
        Log.getLog(log).info("created ConnectionManager.");
    }

    public Connection getMySQLConnection() {
        return this.provider.getMySQLConnection();
    }
}
