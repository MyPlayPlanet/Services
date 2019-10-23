package net.myplayplanet.services.connection.debug;

import lombok.experimental.Delegate;
import net.myplayplanet.services.connection.ConnectionManager;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class PPConnection implements Connection {

    private Connection connection;

    public PPConnection(Connection connection) {
        this.connection = connection;
    }

    private static void addCall(String methodName) {
        methodName.replace(" ", "_");
        try {
            if (!ConnectionManager.getInstance().getStringConnection().async().hgetall("mysql_debugs").get().containsKey(methodName)) {
                ConnectionManager.getInstance().getStringConnection().async().hset("mysql_debugs", methodName, "0");
            }
            int value = Integer.valueOf(ConnectionManager.getInstance().getStringConnection().async().hget("mysql_debugs", methodName).get());
            value++;
            ConnectionManager.getInstance().getStringConnection().async().hset("mysql_debugs", methodName, value + "");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Statement createStatement() throws SQLException {
        addCall("createStatement()");
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        addCall("prepareStatement(String sql)");
        return connection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        addCall("prepareCall(String sql)");
        return connection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        addCall("nativeSQL(String sql)");
        return connection.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        addCall("setAutoCommit(boolean autoCommit)");
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        addCall("getAutoCommit()");
        return connection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        addCall("commit()");
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        addCall("rollback()");
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        addCall("close()");
        connection.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        addCall("isClosed()");
        return connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        addCall("getMetaData()");
        return connection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        addCall("setReadOnly(boolean readOnly)");
        connection.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        addCall("isReadOnly()");
        return connection.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        addCall("setCatalog(String catalog)");
        connection.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        addCall("getCatalog()");
        return connection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        addCall("setTransactionIsolation(int level)");
        connection.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        addCall("getTransactionIsolation()");
        return connection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        addCall("getWarnings()");
        return connection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        addCall("clearWarnings()");
        connection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        addCall("createStatement(int resultSetType, int resultSetConcurrency)");
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        addCall("prepareStatement(String sql, int resultSetType, int resultSetConcurrency)");
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        addCall("prepareCall(String sql, int resultSetType, int resultSetConcurrency)");
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        addCall("getTypeMap()");
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        addCall("setTypeMap(Map<String, Class<?>> map)");
        connection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        addCall("setHoldability(int holdability)");
        connection.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        addCall("getHoldability()");
        return connection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        addCall("setSavepoint()");
        return connection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        addCall("setSavepoint(String name)");
        return connection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        addCall("rollback(Savepoint savepoint)");
        connection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        addCall("releaseSavepoint(Savepoint savepoint)");
        connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        addCall("createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)");
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        addCall("prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)");
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        addCall("prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)");
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        addCall("prepareStatement(String sql, int autoGeneratedKeys)");
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        addCall("prepareStatement(String sql, int[] columnIndexes)");
        return connection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        addCall("prepareStatement(String sql, String[] columnNames)");
        return connection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        addCall("createClob()");
        return connection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        addCall("createBlob()");
        return connection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        addCall("createNClob()");
        return connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        addCall("createSQLXML()");
        return connection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        addCall("isValid(int timeout)");
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        addCall("setClientInfo(String name, String value)");
        connection.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        addCall("setClientInfo(Properties properties)");
        connection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        addCall("getClientInfo(String name)");
        return connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        addCall("getClientInfo()");
        return connection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        addCall("createArrayOf(String typeName, Object[] elements)");
        return connection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        addCall("createStruct(String typeName, Object[] attributes)");
        return connection.createStruct(typeName, attributes);
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        addCall("setSchema(String schema)");
        connection.setSchema(schema);
    }

    @Override
    public String getSchema() throws SQLException {
        addCall("getSchema()");
        return connection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        addCall("abort(Executor executor)");
        connection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        addCall("setNetworkTimeout(Executor executor, int milliseconds)");
        connection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        addCall("getNetworkTimeout()");
        return connection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        addCall("unwrap(Class<T> iface)");
        return connection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        addCall("isWrapperFor(Class<?> iface)");
        return connection.isWrapperFor(iface);
    }
}
