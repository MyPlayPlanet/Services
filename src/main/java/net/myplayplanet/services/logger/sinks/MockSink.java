package net.myplayplanet.services.logger.sinks;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.myplayplanet.services.logger.LogEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockSink implements ISink {
    @Setter
    @Getter
    private static ArrayList<Object> resultOnGetEntries;

    public MockSink() {
        resultOnGetEntries = new ArrayList<>();
    }

    @Override
    public void save(LogEntry entry) {
        System.out.println(new Gson().toJson(entry));
    }

    @Override
    public List<Object> getLogEntrys(Date from, Date to) {
        return resultOnGetEntries;
    }
}
