package net.myplayplanet.services.connection.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.myplayplanet.services.connection.AbstractConnectionManager;
import net.myplayplanet.services.connection.ConnectionSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

public class MySqlManager extends AbstractConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlManager.class);

    @Getter
    private HikariDataSource mysqlDataSource;

    public MySqlManager(ConnectionSetting settings) {
        super(settings);
    }

    @Override
    public void createConnection() {
        String connectionString = String.format("jdbc:mariadb://%s:%s/%s?autoReconnect=true&serverTimezone=%s", this.getSetting().getHostname(), this.getSetting().getPort(), this.getSetting().getDatabase(), TimeZone.getDefault().getID());
        LOGGER.info("creating MariaDB Connection: {}", connectionString);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(connectionString);
        config.setUsername(this.getSetting().getUsername());
        config.setPassword(this.getSetting().getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        this.mysqlDataSource = new HikariDataSource(config);
        this.mysqlDataSource.setMaximumPoolSize(100);
        LOGGER.info("created MariaDB Connection!");
    }

    public Connection get() throws SQLException {
        return mysqlDataSource.getConnection();
    }

    @Override
    public void init() {

    }
}
