package net.myplayplanet.services.connection.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.myplayplanet.services.connection.ConnectionSetting;
import net.myplayplanet.services.connection.AbstractConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

public class MySqlManager extends AbstractConnectionManager {
    private HikariDataSource mysqlDataSource;

    public MySqlManager(ConnectionSetting settings) {
        super(settings);
    }

    @Override
    public void createConnection() {
        System.out.println("creating MySQL Client with hostname " + this.getSetting().getHostname() + "" +
                " port " + this.getSetting().getPort() + " and database " + this.getSetting().getDatabase() + ".");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + this.getSetting().getHostname() + ":" + this.getSetting().getPort() + "/" + this.getSetting().getDatabase()
                + "?autoReconnect=true&serverTimezone=" + TimeZone
                .getDefault().getID());
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(this.getSetting().getUsername());
        config.setPassword(this.getSetting().getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        this.mysqlDataSource = new HikariDataSource(config);
        this.mysqlDataSource.setMaximumPoolSize(100);
        System.out.println("created MySQL Client!");
    }

    public Connection get() throws SQLException {
        return mysqlDataSource.getConnection();
    }

    @Override
    public void init() {

    }
}
