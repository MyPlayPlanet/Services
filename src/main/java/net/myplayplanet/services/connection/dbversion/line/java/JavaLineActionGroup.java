package net.myplayplanet.services.connection.dbversion.line.java;

import net.myplayplanet.services.connection.dbversion.line.ILineAction;
import net.myplayplanet.services.connection.dbversion.line.ILineActionGroup;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class JavaLineActionGroup implements ILineActionGroup {
    private final ArrayList<ILineAction> actions;

    public JavaLineActionGroup() {
        actions = new ArrayList<>();
    }

    @Override
    public ArrayList<ILineAction> getActions() {
        return actions;
    }

    @Override
    public void execute(Connection connection) throws SQLException {
        for (ILineAction action : actions) {
            Statement statement = connection.createStatement();
            action.execute(statement);
        }
    }
}
