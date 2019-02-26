package net.myplayplanet.services.logger;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@ToString
@Builder
public class LogEntry {
    private static Pattern bracePattern = Pattern.compile("\\{(.*?)\\}");

    private Throwable ex;
    private LogLevel level;
    private String message;
    private Logger logger;
    private Date date;
    private HashMap<String, String> content;
    private ArrayList<String> fields;
    private String messageAfterFieldExtraction;
    private String logMessage;

    public LogEntry(Throwable ex, LogLevel level, String message, Logger logger, Date date, Object... args) {
        this.ex = ex;
        this.level = level;
        this.message = message;
        this.logger = logger;
        this.date = date;

        this.fields = new ArrayList<>();

        Matcher matcher = bracePattern.matcher(message);
        while (matcher.find()) {
            String group = matcher.group();
            if (group.isEmpty()) {
                group = "{none}";
            }
            String substring = group.substring(1, group.toCharArray().length - 1);
            fields.add(substring);
        }
        this.messageAfterFieldExtraction = bracePattern.matcher(message).replaceAll("{}");

        if (fields.size() != args.length) {
            throw new IllegalArgumentException(
                    "messsage contains not the same amount of braces then objects! (field size: "
                            + fields.size() + ", object size: " + args.length + ")");
        }

        this.content = new HashMap<>();

        this.logMessage = message;
        for (int i = 0; i < fields.size(); i++) {
            this.logMessage = this.logMessage.replaceFirst(
                    Pattern.quote("{"+fields.get(i)+ "}"), "\"" + args[i].toString() + "\"");
        }

        if (ex != null) {
            this.content.put("exception", new Gson().toJson(ex));
        }

        for (int i = 0; i < fields.size(); i++) {
            this.content.put(fields.get(i), new Gson().toJson(args[i]));
        }
    }
}
