package net.myplayplanet.services.checker.provider;

import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.connection.SQLUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SqlCheckProvider implements ICheckProvider {
    @Override
    public boolean saveBadWord(String badWord) {
        //<editor-fold desc="save single entry to SQL">
        Connection conn = ConnectionManager.getInstance().getMySQLConnection();

        try {
            PreparedStatement statement = conn.prepareStatement("insert into bad_words(`bezeichnung`) VALUES (?)");

            statement.setString(1, badWord);
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
    public HashMap<String, String> loadBadWords() {
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
            statement.closeOnCompletion();
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
    public List<String> saveAllBadWords(HashMap<String, String> values) {
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
                            "ON DUPLICATE KEY UPDATE bezeichnung=values(bezeichnung)");


            int index = 1;
            for (String string : values.values()) {
                statement.setString(index++, string.toUpperCase());
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

    @Override
    public boolean savePermutation(String badWord, String permutation) {
        //<editor-fold desc="save everything to sql">
        Connection conn = ConnectionManager.getInstance().getMySQLConnection();
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO `bad_words_permutaitons` " +
                            "(`word_bezeichnung`, " +
                            "`bezeichung`) " +
                            "VALUES (?, ?)" +
                            "ON DUPLICATE KEY UPDATE bezeichung=values(bezeichung)");
            statement.setString(1, badWord.toUpperCase());
            statement.setString(2, permutation.toUpperCase());

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
    public HashMap<String, String> loadPermutations(String badWord) {
        //<editor-fold desc="load everything from sql">
        Connection conn = ConnectionManager.getInstance().getMySQLConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT bezeichung FROM bad_words_permutaitons WHERE word_bezeichnung = ?; ");
            statement.setString(1, badWord);
            ResultSet set = statement.executeQuery();

            HashMap<String, String> result = new HashMap<>();

            while (set.next()) {
                String bezeichung = set.getString("bezeichung");
                result.put(bezeichung, bezeichung);
            }
            statement.closeOnCompletion();
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
    public List<String> saveAllPermutations(String badWord, HashMap<String, String> values) {
        if (values.size() == 0) {
            return new ArrayList<>();
        }


        //<editor-fold desc="save everything to sql">
        Connection conn = ConnectionManager.getInstance().getMySQLConnection();
        try {
            PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO `bad_words_permutaitons` " +
                            "(`word_bezeichnung`, " +
                            "`bezeichung`) " +
                            "VALUES " + SQLUtils.buildValuesString(2, values.size()) +
                            "ON DUPLICATE KEY UPDATE bezeichung=values(bezeichung)");

            int index = 1;
            for (String string : values.values()) {
                statement.setString(index++, badWord.toUpperCase());
                statement.setString(index++, string.toUpperCase());
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

    @Override
    public boolean remove(String badWord) {
        Connection connection = ConnectionManager.getInstance().getMySQLConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement deleteBadWords = connection.prepareStatement("DELETE FROM bad_words WHERE bezeichnung=?");
            deleteBadWords.setString(1, badWord.toUpperCase());
            deleteBadWords.executeQuery();
            PreparedStatement deletePermutations = connection.prepareStatement("DELETE FROM bad_words_permutaitons WHERE word_bezeichnung=?");
            deletePermutations.setString(1, badWord.toUpperCase());
            deletePermutations.executeQuery();
            deleteBadWords.closeOnCompletion();
            connection.commit();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }finally {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
