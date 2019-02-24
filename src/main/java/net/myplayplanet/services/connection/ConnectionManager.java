package net.myplayplanet.services.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.logger.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

@Slf4j
public class ConnectionManager {
    @Getter
    private static ConnectionManager instance;
    @Getter
    private StatefulRedisConnection<byte[], byte[]> redisConnection;
    @Getter
    private StatefulRedisPubSubConnection<byte[], byte[]> redisPubSubConnection;
    @Getter
    private StatefulRedisConnection<String, String> redisSConnection;
    @Getter
    private StatefulRedisPubSubConnection<String, String> redisSPubSubConnection;
    private ConnectionSettings redisSetting;
    private ConnectionSettings mysqlSetting;
    private HikariDataSource mysqlDataSource;

    //TODO: Logger

    protected ConnectionManager(ConnectionSettings redisSetting, ConnectionSettings mysqlSetting) {
        Log.getLog(log).info("creating ConnectionManager.");
        instance = this;
        this.redisSetting = redisSetting;
        this.mysqlSetting = mysqlSetting;

        this.createRedisConnection();
        this.createMySQLClient();
        Log.getLog(log).info("created ConnectionManager.");
    }

    private void createMySQLClient() {
        Log.getLog(log).debug("creating MySQL Client with hostname  {hostname} port {port} and database {datebase}.",
                this.mysqlSetting.getHostname(), this.mysqlSetting.getPort(), this.mysqlSetting.getDatabase());
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + this.mysqlSetting.getHostname() + ":" + this.mysqlSetting.getPort() + "/" + this.mysqlSetting.getDatabase()
                + "?autoReconnect=true&serverTimezone=" + TimeZone
                .getDefault().getID());
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(this.mysqlSetting.getUsername());
        config.setPassword(this.mysqlSetting.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        this.mysqlDataSource = new HikariDataSource(config);
        int size = 2000000;
        this.mysqlDataSource.setMaximumPoolSize(size);
        Log.getLog(log).debug("set maxPoolSize to {size}", size);
        Log.getLog(log).info("created MySQL Client!");
    }

    private void createRedisConnection() {
        Log.getLog(log).debug("creating Redis Connection with hostname  {hostname} port {port} and database {datebase}.",
                this.redisSetting.getHostname(), this.redisSetting.getPort(), this.redisSetting.getDatabase());

        RedisURI redisUri;
        if (this.redisSetting.getPassword() == null) {
            redisUri = RedisURI.Builder.redis(this.redisSetting.getHostname()).withPort(this.redisSetting.getPort()).build();
        } else {
            redisUri = RedisURI.Builder.redis(this.redisSetting.getHostname()).withPort(this.redisSetting.getPort()).withPassword(this.redisSetting.getPassword()).build();
        }

        RedisClient redisClient = RedisClient.create(redisUri);
        RedisClient redisClient2 = RedisClient.create(redisUri);

        this.redisConnection = redisClient.connect(new ByteArrayCodec());
        this.redisPubSubConnection = redisClient.connectPubSub(new ByteArrayCodec());
        this.redisSConnection = redisClient2.connect(new StringCodec());
        this.redisSPubSubConnection = redisClient2.connectPubSub(new StringCodec());
        Log.getLog(log).info("created Redis Connection!");
    }

    public Connection getMySQLConnection() {
        try {
            return this.mysqlDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
