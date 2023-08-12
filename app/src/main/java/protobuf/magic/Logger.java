package protobuf.magic;

import burp.api.montoya.logging.Logging;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {
  static Logging instance;
  static String className;

  public Logger(Class<?> clazz) {
    className = clazz.getName();
  }

  public void setInstance(Logging instance) {
    Logger.instance = instance;
  }

  public void logToOutput(String message) {
    instance.logToOutput(className + ": " + message);
  }

  public void logToError(String message) {
    instance.logToError(className + ": " + message);
  }

  public void logToError(Exception e) {
    instance.logToError(className + ": " + getStackTraceAsString(e));
  }

  public void raiseErrorEvent(String message) {
    instance.raiseErrorEvent(className + ": " + message);
  }

  public void raiseErrorEvent(Exception e) {
    instance.raiseErrorEvent(className + ": " + getStackTraceAsString(e));
  }

  public void raiseCriticalEvent(String message) {
    instance.raiseCriticalEvent(className + ": " + message);
  }

  public void raiseInfoEvent(String message) {
    instance.raiseInfoEvent(className + ": " + message);
  }

  public void raiseDebugEvent(String message) {
    instance.raiseDebugEvent(className + ": " + message);
  }

  private String getStackTraceAsString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
