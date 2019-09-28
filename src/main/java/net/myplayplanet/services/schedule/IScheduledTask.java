package net.myplayplanet.services.schedule;

import java.util.concurrent.TimeUnit;

public interface IScheduledTask {

    TimeUnit getIntervalUnit();
    int getInterval();
    void runLater();
}
