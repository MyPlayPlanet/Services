package net.myplayplanet.services.connection;

public class SQLUtils {
    /**
     * @param amountOfDifferentValues the amount of Questionsmarks. In the example it would be 4.
     * @param amountOfEntries         the amount of Value Strings. In the example it would be 3.
     * @return the whole Values String.
     * example: (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?)
     */
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

    /**
     * @param valueAmount the amount of question Marks. in the example it would be 3.
     * @return the Values String needed for SQL.
     * example: (?, ?, ?)
     */
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
