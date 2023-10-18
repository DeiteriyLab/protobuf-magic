package protobuf.magic.protobuf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class OffsetBytesAppender {
  public static List<Byte> append(int offset, List<Byte> payload) {
    ByteBuffer buffer = ByteBuffer.allocate(offset + payload.size() + 1);
    for (int i = 0; i < offset - 1; ++i) {
      buffer.put((byte) 0);
    }
    buffer.put((byte) payload.size());
    byte[] bytes = toArray(payload);
    buffer.put(bytes);
    List<Byte> list = toList(buffer.array());
    return list;
  }

  private static byte[] toArray(List<Byte> bufferList) {
    byte[] buffer = new byte[bufferList.size()];
    for (int i = 0; i < bufferList.size(); i++) {
      buffer[i] = bufferList.get(i);
    }
    return buffer;
  }

  private static List<Byte> toList(byte[] buffer) {
    List<Byte> list = new ArrayList<>();
    for (byte b : buffer) {
      list.add(b);
    }
    return list;
  }
}
