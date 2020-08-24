package net.myplayplanet.services.connection.dbversion.line;

import java.sql.SQLException;
import java.sql.Statement;

public interface ILineAction {
    String getContent();
    void execute(Statement statement) throws SQLException;
}
