package protobuf.magic.handler;

import java.util.ArrayList;
import java.util.List;

public class EmptyHandler extends ByteHandler {
  @Override
  public List<Byte> handle(String str) {
    return new ArrayList<>();
  }
}
