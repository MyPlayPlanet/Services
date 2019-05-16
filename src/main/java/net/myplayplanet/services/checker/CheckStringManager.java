package net.myplayplanet.services.checker;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.cache.AbstractSaveProvider;
import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.connection.SQLUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Getter
@Slf4j
public class CheckStringManager {

    @Getter
    private static CheckStringManager instance;

    private Cache<String, Integer> wordCache;
    private Cache<String, ArrayList<String>> permutationCache;

    public CheckStringManager() {
        instance = this;
        //todo when adding implement auto increment implementation so there is no word_id needed for inserting.

        wordCache = new Cache<>("badword-cache", s -> {
            Connection conn = ConnectionManager.getInstance().getMySQLConnection();

            try {
                PreparedStatement statement = conn.prepareStatement("select word_id from bad_words where bezeichnung = ?; ");
                statement.setString(1, s);
                ResultSet set = statement.executeQuery();

                int wordId = -1;

                while (set.next()) {
                    wordId = set.getInt("word_id");
                    break;
                }

                if (wordId == -1) {
                    return null;
                }

                return wordId;
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
        }, new AbstractSaveProvider<String, Integer>() {
            @Override
            public boolean save(@NonNull String key, Integer value) {
                Connection conn = ConnectionManager.getInstance().getMySQLConnection();

                wordCache.loadCache();

                try {
                    PreparedStatement statement = conn.prepareStatement("insert into bad_words(`bezeichnung`, `word_id`) VALUES (?, ?)");

                    statement.setString(1, key);
                    statement.setInt(2, value);

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
            }
        });

        permutationCache = new Cache<>("badword-permutation-cache", s -> {
            Connection conn = ConnectionManager.getInstance().getMySQLConnection();

            try {
                PreparedStatement statement = conn.prepareStatement("select bezeichung from bad_words_permutaitons where word_id = ?; ");
                statement.setInt(1, );
                ResultSet set = statement.executeQuery();

                ArrayList<String> result = new ArrayList<>();

                while (set.next()) {
                    result.add(set.getString("bezeichung"));
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
        }, new AbstractSaveProvider<Integer, ArrayList<String>>() {
            @Override
            public boolean save(Integer key, ArrayList<String> value) {
                Connection conn = ConnectionManager.getInstance().getMySQLConnection();

                try {
                    PreparedStatement statement = conn.prepareStatement(
                            "INSERT INTO `bad_words_permutaitons` " +
                                    "(`word_id`, " +
                                    "`bezeichung`) " +
                                    "VALUES " + SQLUtils.buildValuesString(2, value.size()) +
                                    "ON DUPLICATE KEY UPDATE " +
                                    "`amount` = `amount`;");


                    int index = 1;
                    for (String string : value) {
                        statement.setString(index, string);
                        statement.setInt(index + 1, key);
                        index += 2;
                    }

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
            badwordCache.get("smth").put


            if (permutations) {
                HashSet<String> badWords = new HashSet<>();

                new StringGenerator().generate(badWords, word);

                badWords.add(word.toUpperCase());

                Cache<String> stringCache = CachingProvider.getInstance().getCache("bad_words");

                for (String string : badWords) {
                    try {
                        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bad_words_permutaitons (word_id, bezeichung) VALUES (?, ?);");
                        preparedStatement.setInt(1, id);
                        preparedStatement.setString(2, string);
                        preparedStatement.executeUpdate();
                        if (!stringCache.getObjects().contains(string)) {
                            stringCache.add(string);
                        }
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
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
    public List<String> getStrings() {
        Cache<String> stringCache = CachingProvider.getInstance().getCache("bad_words");
        if (stringCache.getObjects().isEmpty()) {
            Connection connection = ConnectionManager.getInstance().getMySQLConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT bezeichung FROM bad_words_permutaitons");

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    stringCache.add(resultSet.getString("bezeichung").toUpperCase());
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        return stringCache.getObjects();
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