package net.myplayplanet.services.connection.dbversion.line.java;

import net.myplayplanet.services.connection.dbversion.line.ILineAction;
import net.myplayplanet.services.internal.api.AbstractJavaSqlScript;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;

public class JavaLineAction implements ILineAction {
    private final AbstractJavaSqlScript abstractJavaSqlScript;
    private final Connection connection;
    private final String content;
    private final Method method;

    public JavaLineAction(AbstractJavaSqlScript abstractJavaSqlScript, Connection connection, String content, Method method) {
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
    public void execute(Statement statement) {
        try {
            System.out.println("invoking method: " + method.getName());
            method.invoke(abstractJavaSqlScript, connection);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
