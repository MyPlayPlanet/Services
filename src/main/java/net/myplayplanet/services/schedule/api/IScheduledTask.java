package net.myplayplanet.services.schedule.api;

import java.util.concurrent.TimeUnit;

public interface IScheduledTask {

    TimeUnit getIntervalUnit();

    int getInterval();

    void runLater();
}
