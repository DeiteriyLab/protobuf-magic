package protobuf.magic.handler;

import protobuf.magic.exception.*;

public abstract class Handler<U, T> {
  protected Handler<U, T> next;

  public Handler<U, T> setNext(Handler<U, T> next) {
    this.next = next;
    return this;
  }

  public U handle(T value) throws NoNextHandlerException, ProcessHandlerException {
    if (next == null) {
      throw new NoNextHandlerException();
    }
    return next.makeHandler(value);
  }

  protected abstract U makeHandler(T value) throws ProcessHandlerException;
}
