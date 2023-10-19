package protobuf.magic.logger;

public interface ProxyLogger {
  void logToError(String message);

  void logToError(String message, Throwable cause);

  void logToError(Throwable cause);

  void logToOutput(String message);

  void raiseCriticalEvent(String message);

  void raiseDebugEvent(String message);

  void raiseErrorEvent(String message);

  void raiseInfoEvent(String message);
}
