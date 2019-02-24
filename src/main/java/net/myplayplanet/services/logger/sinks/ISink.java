package net.myplayplanet.services.logger.sinks;


import net.myplayplanet.services.logger.LogEntry;

public interface ISink {
    void save(LogEntry entry);
}
