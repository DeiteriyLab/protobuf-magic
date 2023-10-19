package protobuf.magic.logger;

public class StdLogger implements ProxyLogger {
  @Override
  public void logToError(String message) {
    System.err.println(message);
  }

  @Override
  public void logToError(String message, Throwable cause) {
    System.err.println(String.format("%s: %s", message, cause));
  }

  @Override
  public void logToError(Throwable cause) {
    System.err.println(cause.getMessage());
  }

  @Override
  public void logToOutput(String message) {
    System.out.println(message);
  }

  @Override
  public void raiseCriticalEvent(String message) {
    System.err.println(message);
  }

  @Override
  public void raiseDebugEvent(String message) {
    System.out.println(message);
  }

  @Override
  public void raiseErrorEvent(String message) {
    System.err.println(message);
  }

  @Override
  public void raiseInfoEvent(String message) {
    System.out.println(message);
  }
}
