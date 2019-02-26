package net.myplayplanet.services.logger.sinks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.myplayplanet.services.logger.LogLevel;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class MySQLEntry {
    private String id;
    private Date date;
    private LogLevel level;
    private String className;
    private String message;
}
