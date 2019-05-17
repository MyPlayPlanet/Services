package net.myplayplanet.services.checker;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.cache.AbstractSaveProvider;
import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.cache.advanced.CacheCollectionSaveProvider;
import net.myplayplanet.services.cache.advanced.ListCache;
import net.myplayplanet.services.cache.advanced.ListCacheCollection;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.connection.SQLUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Getter
@Slf4j
public class CheckStringManager {

    @Getter
    private static CheckStringManager instance;

    private ListCache<String, String> wordCache;
    private ListCacheCollection<String, String, String> permutationCacheCollection;

    public CheckStringManager() {
        instance = this;

        wordCache = new ListCache<>("badword-cache",
                s -> s,
                new AbstractSaveProvider<String, String>() {
                    @Override
                    public boolean save(String key, String value) {
                        //<editor-fold desc="save single entry to SQL">
                        Connection conn = ConnectionManager.getInstance().getMySQLConnection();

                        try {
                            PreparedStatement statement = conn.prepareStatement("insert into bad_words(`bezeichnung`) VALUES (?)");

                            statement.setString(1, value);
                            statement.executeUpdate();
                            return true;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        } finally {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        //</editor-fold>
                    }

                    @Override
                    public HashMap<String, String> load() {
                        //<editor-fold desc="load everything from sql">
                        Connection conn = ConnectionManager.getInstance().getMySQLConnection();

                        try {
                            PreparedStatement statement = conn.prepareStatement("select bezeichnung from bad_words");

                            ResultSet set = statement.executeQuery();

                            HashMap<String, String> result = new HashMap<>();

                            while (set.next()) {
                                String value = set.getString("bezeichnung");
                                result.put(value, value);
                            }

                            return result;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return null;
                        } finally {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        //</editor-fold>
                    }

                    @Override
                    public List<String> saveAll(HashMap<String, String> values) {
                        if (values.size() == 0) {
                            return new ArrayList<>();
                        }

                        //<editor-fold desc="save everything to sql">
                        Connection conn = ConnectionManager.getInstance().getMySQLConnection();
                        try {
                            PreparedStatement statement = conn.prepareStatement(
                                    "INSERT INTO `bad_words` " +
                                            "(`bezeichnung`) " +
                                            "VALUES " + SQLUtils.buildValuesString(1, values.size()) +
                                            "ON DUPLICATE KEY UPDATE " +
                                            "`amount` = `amount`;");


                            int index = 1;
                            for (String string : values.values()) {
                                statement.setString(index, string);
                                index += 1;
                            }

                            statement.executeUpdate();
                            statement.closeOnCompletion();
                            return new ArrayList<>(values.values());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return new ArrayList<>();
                        } finally {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        //</editor-fold>
                    }
                },
                s -> s
        );

        permutationCacheCollection = new ListCacheCollection<>("badword-permutation-cache",
                (integer, s) -> s,
                (integer, s) -> s,
                new CacheCollectionSaveProvider<String, String, String>() {
                    @Override
                    public boolean save(String masterKey, String key, String value) {
                        //<editor-fold desc="save everything to sql">
                        Connection conn = ConnectionManager.getInstance().getMySQLConnection();
                        try {
                            PreparedStatement statement = conn.prepareStatement(
                                    "INSERT INTO `bad_words_permutaitons` " +
                                            "(`word_id`, " +
                                            "`bezeichung`) " +
                                            "VALUES (?, ?)" +
                                            "ON DUPLICATE KEY UPDATE " +
                                            "`amount` = `amount`;");
                            statement.setString(1, masterKey);
                            statement.setString(2, value);

                            statement.executeUpdate();
                            statement.closeOnCompletion();
                            return true;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return false;
                        } finally {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        //</editor-fold>
                    }

                    @Override
                    public HashMap<String, String> load(String masterKey) {
                        //<editor-fold desc="load everything from sql">
                        Connection conn = ConnectionManager.getInstance().getMySQLConnection();
                        try {
                            PreparedStatement statement = conn.prepareStatement("select bezeichung from bad_words_permutaitons where word_id = ?; ");
                            statement.setString(1, masterKey);
                            ResultSet set = statement.executeQuery();

                            HashMap<String, String> result = new HashMap<>();

                            while (set.next()) {
                                String bezeichung = set.getString("bezeichung");
                                result.put(bezeichung, bezeichung);
                            }

                            return (result.size() > 0) ? result : null;
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return null;
                        } finally {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        //</editor-fold>
                    }

                    @Override
                    public List<String> saveAll(String masterKey, HashMap<String, String> values) {
                        if (values.size() == 0) {
                            return new ArrayList<>();
                        }


                        //<editor-fold desc="save everything to sql">
                        Connection conn = ConnectionManager.getInstance().getMySQLConnection();
                        try {
                            PreparedStatement statement = conn.prepareStatement(
                                    "INSERT INTO `bad_words_permutaitons` " +
                                            "(`word_id`, " +
                                            "`bezeichung`) " +
                                            "VALUES " + SQLUtils.buildValuesString(2, values.size()) +
                                            "ON DUPLICATE KEY UPDATE " +
                                            "`amount` = `amount`;");


                            int index = 1;
                            for (String string : values.values()) {
                                statement.setString(index, string);
                                statement.setString(index + 1, masterKey);
                                index += 2;
                            }

                            statement.executeUpdate();
                            statement.closeOnCompletion();
                            return new ArrayList<>(values.values());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            return new ArrayList<>();
                        } finally {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        //</editor-fold>
                    }
                });
    }

    public void add(String word) {
        this.add(word, true);
    }

    /**
     * Adds an String to the Bad String List in MySQL
     * Also the String to the Cache
     *
     * @param word Which should be added
     */
    public void add(String word, boolean permutations) {
        ForkJoinPool.commonPool().execute(() -> {
            wordCache.addItem(word);


            if (permutations) {
                HashSet<String> badWords = new HashSet<>();

                new StringGenerator().generate(badWords, word);

                badWords.add(word.toUpperCase());

                ListCache<String, String> cache = permutationCacheCollection.getCache(word);
                for (String badWord : badWords) {
                    cache.addItem(badWord);
                }
            }
        });
    }

    /**
     * Removes an String from the Bad String List
     *
     * @param string Which should be removed
     * @return {@link Boolean#TRUE} if successful
     */
    public boolean remove(String string) {
        if (!(check(string))) {
            return false;
        }

        wordCache.removeItem(string);
        permutationCacheCollection.getCache(string).clear();

        Connection connection = ConnectionManager.getInstance().getMySQLConnection();

        int id = -1;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM bad_words WHERE bezeichnung=?");
            preparedStatement.setString(1, string);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("id");
                break;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (id == -1) {
            return false;
        }

        try {
            PreparedStatement deleteBadWords = connection.prepareStatement("DELETE FROM bad_words WHERE id=?");
            deleteBadWords.setInt(1, id);
            deleteBadWords.executeQuery();
            PreparedStatement deletePermutations = connection.prepareStatement("DELETE FROM bad_words_permutaitons WHERE word_id=?");
            deletePermutations.setInt(1, id);
            deletePermutations.executeQuery();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Gets all Strings from the Cache and loads them from MySQL to the Cache if
     * the Cache is Empty
     *
     * @return {@link List<String>} with all Strings from the List
     */
    public HashSet<String> getStrings() {
        HashSet<String> result = new HashSet<>();

        for (String s : wordCache.getList()) {
            result.addAll(permutationCacheCollection.getCache(s).getList());
        }
        return result;
    }

    /**
     * Checks if an String is on the Bad String List
     *
     * @param string Which should be Checked
     * @return {@link Boolean#TRUE} when the String is on the Bad String List
     */
    public boolean check(String string) {
        String message = this.removeDuplicatLetters(this.removeSpecialCharacters(string));
        for (String s : this.getStrings()) {
            if (message.contains(s.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public HashSet<String> getBadWords(String string) {
        HashSet<String> set = new HashSet<>();
        String message = this.removeDuplicatLetters(this.removeSpecialCharacters(string));
        for (String s : this.getStrings()) {
            if (message.contains(s.toUpperCase())) {
                set.add(s.toUpperCase());
            }
        }
        return set;
    }

    /**
     * Checks if an String is on the Bad String List
     *
     * @param string Which should be Checked
     * @return {@link Boolean#TRUE} when the String is on the Bad String List
     */
    public String removeDuplicatLetters(String string) {
        List<Character> chars = new ArrayList<>();

        String trimedString = string.toUpperCase().trim().replace(" ", "");

        char lastChar = 0;
        for (char c : trimedString.toCharArray()) {
            if (lastChar != c) {
                chars.add(c);
            }
            lastChar = c;
        }

        String shortedString = "";

        for (Character c : chars) {
            shortedString += c;
        }

        return shortedString.toUpperCase();
    }

    /**
     * Removes all Special Characters
     *
     * @param string Which should be Checked
     * @return The String without Special Characters
     */
    public String removeSpecialCharacters(String string) {
        String trimedString = string.toUpperCase().trim().replace(" ", "");

        StringBuilder stringBuilder = new StringBuilder();

        for (char c : trimedString.toCharArray()) {
            for (Letters value : Letters.values()) {
                char c1 = value.name().charAt(0);
                if (c1 == c || value.contains(value, c)) {
                    stringBuilder.append(c);
                }
            }
        }

        return stringBuilder.toString().toUpperCase();
    }

    /**
     * Normal String without LeetSpeak
     *
     * @param leetSpeakString The String in Leetspeak
     * @return Normal String
     */
    public String getNormalString(String leetSpeakString) {
        String message = "";

        String replaceString = leetSpeakString.replace(" ", "").toUpperCase();

        for (char c : replaceString.toCharArray()) {
            message += Letters.getOriginalChar(c);
        }
        return message.toUpperCase();
    }
}