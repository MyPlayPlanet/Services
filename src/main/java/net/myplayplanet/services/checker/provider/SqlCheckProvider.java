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
                            "ON DUPLICATE KEY UPDATE " +
                            "`bezeichnung` = `bezeichnung`;");


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

    @Override
    public boolean savePermutation(String badWord, String permutation) {
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
            statement.setString(1, badWord);
            statement.setString(2, permutation);

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
            PreparedStatement statement = conn.prepareStatement("select bezeichung from bad_words_permutaitons where word_id = ?; ");
            statement.setString(1, badWord);
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
    public List<String> saveAllPermutations(String badWord, HashMap<String, String> values) {
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
                statement.setString(index + 1, badWord);
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

    @Override
    public boolean remove(String badWord) {
        Connection connection = ConnectionManager.getInstance().getMySQLConnection();

        int id = -1;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM bad_words WHERE bezeichnung=?");
            preparedStatement.setString(1, badWord);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("id");
                break;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        if (id == -1) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            return false;
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
