package protobuf.magic.protobuf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class OffsetBytesAppender {
  public static List<Byte> append(int offset, List<Byte> payload) {
    ByteBuffer buffer = ByteBuffer.allocate(4 + 1 + payload.size());
    buffer.putInt(0);
    buffer.put((byte) offset);
    byte[] bytes = new byte[payload.size()];
    for (int i = 0; i < payload.size(); i++) {
      bytes[i] = payload.get(i);
    }
    buffer.put(bytes);
    List<Byte> list = new ArrayList<>();
    for (byte b : buffer.array()) {
      list.add(b);
    }
    return list;
  }
}
