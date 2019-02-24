package net.myplayplanet.services.logger;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Calendar;
import java.util.concurrent.ForkJoinPool;

@AllArgsConstructor
public class Logger {
  @NonNull
  private org.slf4j.Logger logger;

  public void log(Throwable ex, LogLevel level, String message, Object... args) {
    ForkJoinPool.commonPool().execute(() -> {
      LogEntry logEntry = new LogEntry(ex, level, message, logger, Calendar.getInstance().getTime(), args);

      Log.getSink().save(logEntry);

      if (ex == null) {
        switch (level) {
          case DEBUG:
            logger.debug(logEntry.getMessageAfterFieldExtraction(), args);
            break;
          case INFO:
            logger.info(logEntry.getMessageAfterFieldExtraction(), args);
            break;
          case WARNING:
            logger.warn(logEntry.getMessageAfterFieldExtraction(), args);
            break;
          case ERROR:
            logger.error(logEntry.getMessageAfterFieldExtraction(), args);
            break;
        }
      }else {
        switch (level) {
          case DEBUG:
            logger.debug(logEntry.getMessageAfterFieldExtraction(), ex);
            logger.debug(logEntry.getMessageAfterFieldExtraction(), args);
            break;
          case INFO:
            logger.info(logEntry.getMessageAfterFieldExtraction(), args);
            break;
          case WARNING:
            logger.warn(logEntry.getMessageAfterFieldExtraction(), args);
            break;
          case ERROR:
            logger.error(logEntry.getMessageAfterFieldExtraction(), args);
            break;
        }
      }
    });
  }

  public void log(LogLevel level, String message, Object... args) {
    this.log(null, level, message, args);
  }


  public void debug(Throwable ex, String message, Object... args) {
    log(ex, LogLevel.DEBUG, message, args);
  }

  public void info(Throwable ex, String message, Object... args) {
    log(ex, LogLevel.INFO, message, args);
  }

  public void warning(Throwable ex, String message, Object... args) {
    log(ex, LogLevel.WARNING, message, args);
  }

  public void error(Throwable ex, String message, Object... args) {
    log(ex, LogLevel.ERROR, message, args);
  }


  public void debug(String message, Object... args) {
    log(null, LogLevel.DEBUG, message, args);
  }

  public void info(String message, Object... args) {
    log(null, LogLevel.INFO, message, args);
  }

  public void warning(String message, Object... args) {
    log(null, LogLevel.WARNING, message, args);
  }

  public void error(String message, Object... args) {
    log(null, LogLevel.ERROR, message, args);
  }


}