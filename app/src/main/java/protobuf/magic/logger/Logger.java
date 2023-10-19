package protobuf.magic.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

// net.portswigger.burp.extensions:montoya-api:2023.1
// don't understand how to change level of logging
// some level work through std output
public class Logger {
  private static final String PATTERN = "%s [%s]: %s";
  private static ProxyLogger instance = new StdLogger();
  private String name;

  public Logger(String name) {
    this.name = name;
  }

  public void setInstance(ProxyLogger instance) {
    Logger.instance = instance;
  }

  public void output(String message) {
    output(message, "OUTPUT");
  }

  public void output(String message, String level) {
    instance.logToOutput(String.format(PATTERN, level, name, message));
  }

  public void output(Exception e, String level) {
    instance.logToOutput(String.format(PATTERN, level, name, getStackTraceAsString(e)));
  }

  public void logError(String message) {
    instance.logToError(String.format(PATTERN, "ERROR", name, message));
  }

  public void logError(Exception e) {
    instance.logToError(String.format(PATTERN, "ERROR", name, getStackTraceAsString(e)));
  }

  public void error(String message) {
    // instance.raiseErrorEvent(String.format(PATTERN, "ERROR", name, message));
    output(message, "ERROR");
  }

  public void error(Exception e) {
    // instance.raiseErrorEvent(String.format(PATTERN, "ERROR", name, getStackTraceAsString(e)));
    output(e, "ERROR");
  }

  public void critical(String message) {
    // instance.raiseCriticalEvent(String.format(PATTERN, "CRITICAL", name, message));
    output(message, "CRITICAL");
  }

  public void info(String message) {
    // instance.raiseInfoEvent(String.format(PATTERN, "INFO", name, message));
    output(message, "INFO");
  }

  public void debug(String message) {
    // instance.raiseDebugEvent(String.format(PATTERN, "DEBUG", name, message));
    output(message, "DEBUG");
  }

  private String getStackTraceAsString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
