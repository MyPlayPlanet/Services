package net.myplayplanet.services.connection.dbversion.line;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public interface ILineActionGroup {
    ArrayList<ILineAction> getActions();

    void execute(Connection connection) throws SQLException;
}
