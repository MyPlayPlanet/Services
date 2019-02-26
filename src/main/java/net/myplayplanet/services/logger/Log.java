package net.myplayplanet.services.logger;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.logger.sinks.ISink;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;

@Slf4j
public class Log {
  @Getter
  private static boolean configSet = false;
  @Getter
  @NonNull
  private static ISink sink;

  public static void initialize(ISink sink) {
    Log.sink = sink;
    initSlf4jConfig();
  }

  public static net.myplayplanet.services.logger.Logger getLog(Logger logger) {
    if (!configSet) {
      initSlf4jConfig();
    }
    return new net.myplayplanet.services.logger.Logger(logger);
  }

  private static void initSlf4jConfig() {
    BasicConfigurator.configure();
    configSet = true;
    Log.getLog(log).info("initialised logging!");
  }
}