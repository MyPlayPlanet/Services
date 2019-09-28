package net.myplayplanet.services.connection.provider;

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
import net.myplayplanet.services.connection.ConnectionSettings;
import net.myplayplanet.services.logger.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

@Slf4j
public class SqlRedisConnectionProvider implements IConnectionProvider {
    @Getter
    private StatefulRedisConnection<byte[], byte[]> byteConnection;
    @Getter
    private StatefulRedisPubSubConnection<byte[], byte[]> bytePubSubConnection;
    @Getter
    private StatefulRedisConnection<String, String> stringConnection;
    @Getter
    private StatefulRedisPubSubConnection<String, String> stringPubSubConnection;
    private ConnectionSettings redisSetting;
    private ConnectionSettings mysqlSetting;
    private HikariDataSource mysqlDataSource;

    public SqlRedisConnectionProvider(ConnectionSettings redisSetting, ConnectionSettings mysqlSetting) {
        this.redisSetting = redisSetting;
        this.mysqlSetting = mysqlSetting;

        try {
            this.createRedisConnection();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        this.createMySQLClient();
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

    private void createRedisConnection() throws ExecutionException, InterruptedException {
        Log.getLog(log).debug("creating Redis Connection with hostname  {hostname} port {port} and database {datebase}.",
                this.redisSetting.getHostname(), this.redisSetting.getPort(), this.redisSetting.getDatabase());

        RedisURI redisUri;
        if (this.redisSetting.getPassword() == null) {
            redisUri = RedisURI.Builder.redis(this.redisSetting.getHostname()).withPort(this.redisSetting.getPort()).build();
        } else {
            redisUri = RedisURI.Builder.redis(this.redisSetting.getHostname()).withPort(this.redisSetting.getPort()).withPassword(this.redisSetting.getPassword()).build();
        }

        RedisClient redisClient = RedisClient.create(redisUri);

        this.byteConnection = redisClient.connect(new ByteArrayCodec());
        this.bytePubSubConnection = redisClient.connectPubSub(new ByteArrayCodec());
        this.stringConnection = redisClient.connect(new StringCodec());
        this.stringPubSubConnection = redisClient.connectPubSub(new StringCodec());

        Log.getLog(log).info("Testing Byte Connection: {ping}", this.byteConnection.async().ping().get());
        Log.getLog(log).info("Testing BytePubSub Connection: {ping}", this.bytePubSubConnection.async().ping().get());
        Log.getLog(log).info("Testing String Connection: {ping}", this.stringConnection.async().ping().get());
        Log.getLog(log).info("Testing StringPubSub Connection: {ping}", this.stringPubSubConnection.async().ping().get());

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
