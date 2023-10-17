package protobuf.magic.exception;

public class UnknownStructException extends Exception {
  public UnknownStructException(String message) {
    super(message);
  }

  public UnknownStructException(Throwable cause) {
    super(cause);
  }
}
