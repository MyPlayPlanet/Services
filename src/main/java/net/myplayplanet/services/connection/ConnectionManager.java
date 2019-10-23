package net.myplayplanet.services.connection;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.connection.debug.PPConnection;
import net.myplayplanet.services.connection.provider.IConnectionProvider;
import net.myplayplanet.services.connection.provider.MockConnectionProvider;
import net.myplayplanet.services.connection.provider.SqlRedisConnectionProvider;
import net.myplayplanet.services.logger.Log;

import java.sql.Connection;

@Slf4j
public class ConnectionManager {
    private IConnectionProvider provider;

    protected ConnectionManager(ConnectionSettings redisSetting, ConnectionSettings mysqlSetting) {
        assert redisSetting != null : "Redis Setting can not be null";
        assert mysqlSetting != null : "SQL Setting can not be null";
        Log.getLog(log).info("creating ConnectionManager.");
        if (ServiceCluster.isDebug()) {
            provider = new MockConnectionProvider();
        }else {
            provider = new SqlRedisConnectionProvider(redisSetting, mysqlSetting);
        }
        Log.getLog(log).info("created ConnectionManager.");
    }

    public static ConnectionManager getInstance(String database) {
        return ServiceCluster.get(ConnectionService.class).getConnectionManager(database);
    }
    public static ConnectionManager getInstance() {
        return ServiceCluster.get(ConnectionService.class).getConnectionManager();
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
        return new PPConnection(this.provider.getMySQLConnection());
    }
}
