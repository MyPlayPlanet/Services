package net.myplayplanet.services.connection.dbversion.line.prepared;

import net.myplayplanet.services.connection.dbversion.line.ILineAction;
import net.myplayplanet.services.internal.api.AbstractJavaSqlScript;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class GeneratePreparedStatementLineAction implements ILineAction {
    private final AbstractJavaSqlScript abstractJavaSqlScript;
    private final Connection connection;
    private final String content;
    private final Method method;

    public GeneratePreparedStatementLineAction(AbstractJavaSqlScript abstractJavaSqlScript, Connection connection, String content, Method method) {
        this.abstractJavaSqlScript = abstractJavaSqlScript;
        this.connection = connection;
        this.content = content;
        this.method = method;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void execute(Statement statement) throws SQLException {
        try {
            System.out.println("invoking generate preparedStatement method: " + method.getName());
            PreparedStatement preparedStatement = (PreparedStatement) method.invoke(abstractJavaSqlScript, connection);
            preparedStatement.execute();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
