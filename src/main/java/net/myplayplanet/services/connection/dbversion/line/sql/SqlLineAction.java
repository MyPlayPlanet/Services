package net.myplayplanet.services.connection.dbversion.line.sql;

import net.myplayplanet.service.core.dbversion.line.ILineAction;

import java.sql.SQLException;
import java.sql.Statement;

public class SqlLineAction implements ILineAction {
    String content;

    public SqlLineAction(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void execute(Statement statement) throws SQLException {
        System.out.println("adding statement to batch:");
        System.out.println(content);
        statement.addBatch(content);
    }
}
