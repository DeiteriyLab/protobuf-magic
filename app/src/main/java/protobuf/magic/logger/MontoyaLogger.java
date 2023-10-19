package protobuf.magic.logger;

import burp.api.montoya.logging.Logging;

public class MontoyaLogger implements ProxyLogger {
  private final Logging logging;

  public MontoyaLogger(Logging logging) {
    this.logging = logging;
  }

  @Override
  public void logToError(String message) {
    logging.logToError(message);
  }

  @Override
  public void logToError(String message, Throwable cause) {
    logging.logToError(String.format("%s: %s", message, cause));
  }

  @Override
  public void logToError(Throwable cause) {
    logging.logToError(cause.getMessage());
  }

  @Override
  public void logToOutput(String message) {
    logging.logToOutput(message);
  }

  @Override
  public void raiseCriticalEvent(String message) {
    logging.raiseCriticalEvent(message);
  }

  @Override
  public void raiseDebugEvent(String message) {
    logging.raiseDebugEvent(message);
  }

  @Override
  public void raiseErrorEvent(String message) {
    logging.raiseErrorEvent(message);
  }

  @Override
  public void raiseInfoEvent(String message) {
    logging.raiseInfoEvent(message);
  }
}
