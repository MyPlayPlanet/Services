package net.myplayplanet.services.connection.dbversion;

import lombok.Getter;
import net.myplayplanet.service.core.api.AbstractJavaSqlScript;
import net.myplayplanet.service.core.dbversion.exception.InvalidReturnTypeException;
import net.myplayplanet.service.core.dbversion.line.ILineAction;
import net.myplayplanet.service.core.dbversion.line.ILineActionGroup;
import net.myplayplanet.service.core.dbversion.line.generate.SqlGeneratorLineAction;
import net.myplayplanet.service.core.dbversion.line.java.JavaLineAction;
import net.myplayplanet.service.core.dbversion.line.java.JavaLineActionGroup;
import net.myplayplanet.service.core.dbversion.line.prepared.GeneratePreparedStatementLineAction;
import net.myplayplanet.service.core.dbversion.line.sql.SqlLineAction;
import net.myplayplanet.service.core.dbversion.line.sql.SqlLineActionGroup;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Getter
public class VersionSqlObject {
    private int version;
    private List<ILineActionGroup> iLineActionGroups;
    private AbstractJavaSqlScript abstractJavaSqlScript;
    private Class<?> clazz;

    public VersionSqlObject(int version, String fileContent, AbstractJavaSqlScript abstractJavaSqlScript, Connection connection) throws NoSuchMethodException, InvalidReturnTypeException {
        this.version = version;
        this.abstractJavaSqlScript = abstractJavaSqlScript;
        this.clazz = abstractJavaSqlScript == null ? null : abstractJavaSqlScript.getClass();
        this.iLineActionGroups = new ArrayList<>();

        ILineActionGroup currentGroup = null;
        for (String line : fileContent.split(";")) {
            String newLine = line.replace("\n", "").trim();

            if (newLine.trim().isEmpty()) {
                System.out.println("skipping empty line...");
                continue;
            }

            if (line.startsWith("#")) {
                System.out.println("skipping comment as line action: " + newLine);
                continue;
            }

            if (newLine.startsWith("java: ")) {
                newLine = newLine.replace("java: ", "").trim();
                Method method = abstractJavaSqlScript.getClass().getMethod(newLine, Connection.class);

                if (method.getReturnType() == PreparedStatement.class) {
                    GeneratePreparedStatementLineAction action = new GeneratePreparedStatementLineAction(abstractJavaSqlScript, connection, newLine, method);
                    currentGroup = processCreateNewGroup(currentGroup, action);
                }

                else if (method.getReturnType() == String.class) {
                    SqlGeneratorLineAction action = new SqlGeneratorLineAction(abstractJavaSqlScript, connection, newLine, method);
                    currentGroup = processAddToGroup(currentGroup, action);
                }

                else if (method.getReturnType() == Void.TYPE) {
                    JavaLineAction action = new JavaLineAction(abstractJavaSqlScript, connection, newLine, method);
                    currentGroup = processCreateNewGroup(currentGroup, action);
                }

                else {
                    throw new InvalidReturnTypeException("invalid return type: " + method.getReturnType());
                }
            } else {
                currentGroup = processAddToGroup(currentGroup, new SqlLineAction(newLine));
            }
        }
        if (currentGroup != null) {
            this.iLineActionGroups.add(currentGroup);
        }
    }

    private ILineActionGroup processAddToGroup(ILineActionGroup currentGroup, ILineAction action) {
        if (currentGroup == null) {
            currentGroup = new SqlLineActionGroup();
        }
        currentGroup.getActions().add(action);
        return currentGroup;
    }

    private ILineActionGroup processCreateNewGroup(ILineActionGroup currentGroup, ILineAction action) {
        if (currentGroup != null) {
            iLineActionGroups.add(currentGroup);
        }

        ILineActionGroup group = new JavaLineActionGroup();
        group.getActions().add(action);
        iLineActionGroups.add(group);
        return null;
    }

    public boolean needsJavaFile() {
        return abstractJavaSqlScript != null;
    }
}
