package protobuf.magic;

public class LockActions {
  static boolean lock = false;

  public LockActions() {}

  boolean isLock() {
    return lock;
  }

  void setLock(boolean lock) {
    LockActions.lock = lock;
  }
}
