package net.myplayplanet.services.connection.dbversion.line.prepared;

import net.myplayplanet.service.core.api.AbstractJavaSqlScript;
import net.myplayplanet.service.core.dbversion.line.ILineAction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class GeneratePreparedStatementLineAction implements ILineAction {
    private AbstractJavaSqlScript abstractJavaSqlScript;
    private Connection connection;
    private String content;
    private Method method;

    public GeneratePreparedStatementLineAction(AbstractJavaSqlScript abstractJavaSqlScript, Connection connection, String content, Method method) throws NoSuchMethodException {
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
