package net.myplayplanet.services.logger.sinks;


import net.myplayplanet.services.logger.LogEntry;

import java.util.Date;
import java.util.List;

public interface ISink {
    void save(LogEntry entry);
    List<Object> getLogEntrys(Date from, Date to);
}
