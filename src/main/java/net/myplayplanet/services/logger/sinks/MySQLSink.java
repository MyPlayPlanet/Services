package net.myplayplanet.services.logger.sinks;

import lombok.NonNull;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.logger.LogEntry;
import net.myplayplanet.services.logger.LogLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MySQLSink implements ISink {
    private HashSet<LogEntry> entrys = new HashSet<>();

    @Override
    public void save(LogEntry entry) {
        try (Connection con = ConnectionManager.getInstance().getMySQLConnection()) {
            for (LogEntry logEntry : entrys) {
                save(logEntry, con);
            }
            entrys = new HashSet<>();

            save(entry, con);

            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException | SQLException ex) {
            this.entrys.add(entry);

            if (entrys.size() > 2500) {
                entry.getLogger().error("maximum amount of cached log messages is reached! {}", 2500);
            }
            return;
        }
    }

    @Override
    public List<Object> getLogEntrys(Date from, Date to) {
        List<Object> entrys = new ArrayList<>();
        try (Connection con = ConnectionManager.getInstance().getMySQLConnection()) {
            Calendar fromCalender = Calendar.getInstance();
            fromCalender.setTime(from);
            fromCalender.add(Calendar.DATE, -1);
            Date before = fromCalender.getTime();

            Calendar toCalender = Calendar.getInstance();
            toCalender.setTime(to);
            toCalender.add(Calendar.DATE, +1);
            Date next = toCalender.getTime();

            PreparedStatement statement = con.prepareStatement("SELECT * FROM `log_entry` WHERE entry_time >= ? AND entry_time <= ?");
            statement.setDate(1, new java.sql.Date(before.getTime()));
            statement.setDate(2, new java.sql.Date(next.getTime()));
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String id = result.getString("id");
                Date date = result.getDate("entry_time");
                LogLevel level = LogLevel.valueOf(result.getString("log_level"));
                String className = result.getString("origin_class");
                String message = result.getString("message");

                entrys.add(new MySQLEntry.MySQLEntryBuilder()
                        .id(id)
                        .date(date)
                        .level(level)
                        .className(className)
                        .message(message)
                        .build());
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entrys;
    }

    private void save(LogEntry entry, Connection con) {
        UUID uuid = createAndInsertLogEntry(con, entry.getDate(), entry.getLevel(),
                entry.getLogger().getName(), entry.getLogMessage());

        entry.getContent().forEach((fieldName, value) -> insertContent(con, fieldName, uuid, value));
    }

    private void insertContent(@NonNull Connection con, @NonNull String fieldName,
                               @NonNull UUID entryId, @NonNull String content) {
        try {
            PreparedStatement statement = con.prepareStatement
                    ("INSERT INTO log_field_content (id, log_entry_id, field_id, content) VALUES (?, ?, ?, ?)");
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, entryId.toString());
            statement.setString(3, fieldName);
            statement.setString(4, content);
            statement.executeUpdate();
            statement.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private UUID createAndInsertLogEntry(@NonNull Connection con, @NonNull Date date,
                                         @NonNull LogLevel level, @NonNull String originClass,
                                         @NonNull String message) {
        UUID uuid = UUID.randomUUID();
        try {
            PreparedStatement statement = con.prepareStatement
                    ("INSERT INTO log_entry (id, entry_time, log_level, origin_class, message) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, uuid.toString());
            statement.setTimestamp(2, new java.sql.Timestamp(date.getTime()));
            statement.setString(3, level.name());
            statement.setString(4, originClass);
            statement.setString(5, message);
            statement.executeUpdate();
            statement.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return uuid;
    }
}

