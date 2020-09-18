package net.myplayplanet.services.connection.dbversion;

import net.myplayplanet.services.config.api.IConfigManager;
import net.myplayplanet.services.config.api.IResourceProvider;
import net.myplayplanet.services.connection.api.IConnectionManager;
import net.myplayplanet.services.connection.dbversion.exception.InvalidReturnTypeException;
import net.myplayplanet.services.connection.dbversion.exception.SetupNotSuccessfulException;
import net.myplayplanet.services.connection.dbversion.line.ILineActionGroup;
import net.myplayplanet.services.connection.provider.MySqlManager;
import net.myplayplanet.services.internal.api.AbstractJavaSqlScript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class UpdateManager {
    private static final String dbInformationTableName = "_mpp_db_information";
    private static final String database = "database";
    private static final String extension = ".sql";

    private final IConfigManager configManager;
    private final IConnectionManager IConnectionManager;
    private final IResourceProvider resourceProvider;

    public UpdateManager(IConfigManager configManager, IConnectionManager IConnectionManager, IResourceProvider resourceProvider) throws SQLException, SetupNotSuccessfulException, IOException {
        this.configManager = configManager;
        this.IConnectionManager = IConnectionManager;
        this.resourceProvider = resourceProvider;
        try (Connection conn = IConnectionManager.get(MySqlManager.class).get()) {
            ensureTableExists(conn);
        }
    }

    public String getDatabaseName() throws IOException {
        return this.configManager.getPropertyFromResource("service", "database-name");
    }

    public void update() {
        try (Connection conn = IConnectionManager.get(MySqlManager.class).get()) {
            conn.setAutoCommit(false);
            internalUpdate(conn);
            conn.commit();
        } catch (SQLException | IOException | SetupNotSuccessfulException e) {
            e.printStackTrace();
        }
    }

    private void internalUpdate(Connection conn) throws SQLException, IOException, SetupNotSuccessfulException {
        int currentVersion = getCurrentVersion(conn);

        for (VersionSqlObject versionSqlObject : getSortedVersionSqlObjects(conn)) {
            if (versionSqlObject.getVersion() <= currentVersion) {
                System.out.println("skipping version " + versionSqlObject.getVersion() + ", already on that version.");
                continue;
            }

            System.out.println("start database update to: " + versionSqlObject.getVersion());

            if (versionSqlObject.needsJavaFile() && versionSqlObject.getAbstractJavaSqlScript().dropDataBase()) {
                String dataBaseBackUp = this.getDatabaseName() + "_backup_" + System.currentTimeMillis();
                conn.prepareStatement("CREATE DATABASE " + dataBaseBackUp).executeUpdate();
                ResultSet result = conn.prepareStatement("show tables;").executeQuery();
                while (result.next()) {
                    String tableName = result.getString(1);
                    conn.prepareStatement("RENAME TABLE " + this.getDatabaseName() + "." + tableName + " TO " + dataBaseBackUp + "." + tableName).executeUpdate();
                }
                conn.prepareStatement("DROP DATABASE " + this.getDatabaseName()).executeUpdate();
                ensureTableExists(conn);
            }

            if (versionSqlObject.needsJavaFile()) {
                versionSqlObject.getAbstractJavaSqlScript().onStart(conn);
            }

            for (ILineActionGroup iLineActionGroup : versionSqlObject.getILineActionGroups()) {
                iLineActionGroup.execute(conn);
            }

            if (versionSqlObject.needsJavaFile()) {
                versionSqlObject.getAbstractJavaSqlScript().onFinish(conn);
            }

            conn.prepareStatement("UPDATE `" + dbInformationTableName + "` SET `value`=" + versionSqlObject.getVersion() + " WHERE `key`=\"version\"").executeUpdate();

            System.out.println("completed version " + versionSqlObject.getVersion());
        }
    }


    public List<VersionSqlObject> getSortedVersionSqlObjects(Connection conn) throws IOException {
        List<VersionSqlObject> result = new ArrayList<>();

        getVersionStreams(database).forEach((versionNumber, inputStream) -> {

            List<String> strings;
            try {
                strings = this.readLinesFromInputStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            StringBuilder scriptBuilder = new StringBuilder();
            AbstractJavaSqlScript abstractJavaSqlScript = null;

            if (strings.size() == 0) {
                System.out.println("updatescript " + versionNumber + ".sql empty.");
                return;
            }

            if (strings.get(0).startsWith("# ClassPath:")) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(strings.get(0).replace("# ClassPath:", "").replace(";", "").trim());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    abstractJavaSqlScript = (AbstractJavaSqlScript) clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                    return;
                }
            }

            for (String string : strings) {
                scriptBuilder.append(string);
                scriptBuilder.append(" ");
            }

            try {
                result.add(new VersionSqlObject(versionNumber, scriptBuilder.toString(), abstractJavaSqlScript, conn));
            } catch (NoSuchMethodException | InvalidReturnTypeException e) {
                e.printStackTrace();
                return;
            }
        });

        result.sort(Comparator.comparingInt(VersionSqlObject::getVersion));
        return result;
    }

    private List<String> readLinesFromInputStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        List<String> lines = new ArrayList<>();

        while (reader.ready()) {
            lines.add(reader.readLine());
        }

        return lines;
    }

    private HashMap<Integer, InputStream> getVersionStreams(String folder) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);

        if (url == null) {
            return new HashMap<>();
        }
        HashMap<Integer, InputStream> files = new HashMap<>();

        int count = 1;
        while (true) {
            String fileName = folder + "/" + count + ".sql";

            InputStream stream = this.resourceProvider.getResourceFile(fileName);

            if (stream == null) {
                break;
            }
            files.put(count, stream);
            count++;
        }
        return files;
    }

    public List<Integer> getVersions() throws IOException {
        return new ArrayList<>(getVersionStreams(database).keySet());
    }

    public int getNewestVersion() throws IOException {
        List<Integer> versionNumbers = getVersions();
        return versionNumbers.size() == 0 ? 0 : versionNumbers.get(versionNumbers.size() - 1);
    }

    public void ensureTableExists(Connection conn) throws SQLException, SetupNotSuccessfulException, IOException {
        String databaseName = getDatabaseName();
        if (databaseName == null || databaseName.trim().isEmpty()) {
            throw new SetupNotSuccessfulException("database name not found, please create a 'service.properties' file with the property 'database-name'");
        }

        ResultSet databases = conn.prepareStatement("SHOW DATABASES").executeQuery();
        boolean databaseFound = false;
        while (databases.next()) {
            String tableName = databases.getString(1);
            if (tableName.equals(databaseName)) {
                databaseFound = true;
                break;
            }
        }

        boolean tableFound = false;
        if (databaseFound) {
            conn.prepareStatement("USE " + databaseName).executeUpdate();
            ResultSet result = conn.prepareStatement("show tables;").executeQuery();
            while (result.next()) {
                String tableName = result.getString(1);
                if (tableName.equals(UpdateManager.dbInformationTableName)) {
                    tableFound = true;
                    break;
                }
            }
        } else {
            conn.prepareStatement("CREATE DATABASE " + databaseName).executeUpdate();
        }

        if (!tableFound) {
            conn.prepareStatement("USE " + databaseName).executeUpdate();
            conn.prepareStatement(
                    "create table " + UpdateManager.dbInformationTableName + " ( `key` VARCHAR(50) NOT NULL , `value` VARCHAR(200) NOT NULL ) ENGINE = InnoDB;").executeUpdate();
            conn.prepareStatement("insert into " + UpdateManager.dbInformationTableName + " (`key`, `value`) VALUES ('version', '0')").executeUpdate();
        }
    }

    public int getCurrentVersion(Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("select `value` from " + dbInformationTableName + " where `key`=? limit 1");
        preparedStatement.setString(1, "version");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return Integer.parseInt(resultSet.getString("value"));
    }

}
