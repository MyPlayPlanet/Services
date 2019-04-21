package net.myplayplanet.services.checker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.cache.CachingProvider;
import net.myplayplanet.services.connection.ConnectionManager;

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

    public CheckStringManager() {
        instance = this;
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
            Connection connection = ConnectionManager.getInstance().getMySQLConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO bad_words (id, bezeichnung) VALUES (NULL, ?);");
                preparedStatement.setString(1, word);
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            int id = -1;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT id from bad_words where bezeichnung =?");
                preparedStatement.setString(1, word);
                ResultSet set = preparedStatement.executeQuery();

                while (set.next()) {
                    id = set.getInt("id");
                    break;
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

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
                            stringCache.getObjects().add(string);
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
            if(message.contains(s.toUpperCase())){
                return true;
            }
        }
        return false;
    }

    public HashSet<String> getBadWords(String string) {
        HashSet<String> set = new HashSet<>();
        String message = this.removeDuplicatLetters(this.removeSpecialCharacters(string));
        for (String s : this.getStrings()) {
            if(message.contains(s.toUpperCase())){
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

        String trimedString = string.trim().replace(" ", "");

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
    public String removeSpecialCharacters(String string){
        String trimedString = string.trim().replace(" ", "");

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