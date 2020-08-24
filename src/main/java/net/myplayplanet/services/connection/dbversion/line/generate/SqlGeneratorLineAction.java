package net.myplayplanet.services.connection.dbversion.line.generate;

import lombok.Getter;
import net.myplayplanet.service.core.api.AbstractJavaSqlScript;
import net.myplayplanet.service.core.dbversion.line.ILineAction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public class SqlGeneratorLineAction implements ILineAction {
    private AbstractJavaSqlScript abstractJavaSqlScript;
    private Connection connection;
    private String content;
    private Method method;

    public SqlGeneratorLineAction(AbstractJavaSqlScript abstractJavaSqlScript, Connection connection, String content, Method method) throws NoSuchMethodException {
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
            System.out.println("invoking generate method: " + method.getName());
            String result = (String) method.invoke(abstractJavaSqlScript, connection);
            statement.addBatch(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
