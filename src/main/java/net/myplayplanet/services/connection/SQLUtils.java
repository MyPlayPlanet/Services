package net.myplayplanet.services.connection;

public class SQLUtils {
    public static String buildValuesString(int amountOfDifferentValues, int amountOfEntries) {
        StringBuilder builder = new StringBuilder();

        String valueString = buildQuestionMarkString(amountOfDifferentValues);

        for (int i = 0; i < amountOfEntries - 1; i++) {
            builder.append(valueString);
            builder.append(", ");
        }
        builder.append(valueString);
        builder.append(" ");
        return builder.toString();
    }

    public static String buildQuestionMarkString(int valueAmount) {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < valueAmount - 1; i++) {
            builder.append("?");
            builder.append(", ");
        }
        builder.append("?)");
        return builder.toString();
    }
}
