package protobuf.magic.handler;

import java.util.List;

public abstract class ByteHandler {
  protected ByteHandler next;

  public ByteHandler setNext(ByteHandler next) {
    this.next = next;
    return this;
  }

  public abstract List<Byte> handle(String str);
}
