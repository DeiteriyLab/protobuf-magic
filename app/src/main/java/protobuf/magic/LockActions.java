package protobuf.magic;

import lombok.CustomLog;

@CustomLog
public class LockActions {
  static boolean lock = false;

  public LockActions() {}

  boolean isLock() {
    return lock;
  }

  void setLock(boolean lock) {
    log.debug("Changed lock to " + lock);
    LockActions.lock = lock;
  }
}
