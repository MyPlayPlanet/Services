package net.myplayplanet.services.connection.dbversion.line.sql;

import net.myplayplanet.service.core.dbversion.line.ILineAction;
import net.myplayplanet.service.core.dbversion.line.ILineActionGroup;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlLineActionGroup implements ILineActionGroup {
    private ArrayList<ILineAction> actions;

    public SqlLineActionGroup() {
        actions = new ArrayList<>();
    }

    @Override
    public ArrayList<ILineAction> getActions() {
        return actions;
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        if (actions.size() == 0) {
            return;
        }
        Statement statement = connection.createStatement();
        for (ILineAction action : actions) {
            action.execute(statement);
        }
        statement.executeBatch();
    }
}
