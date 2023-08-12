package protobuf.magic;

import java.nio.ByteBuffer;

public class LeftOverBytesAppender {
  public static byte[] appendLeftOverBytes(int leftOverValue, byte[] payload) {
    ByteBuffer buffer = ByteBuffer.allocate(4 + 1 + payload.length);
    buffer.putInt(0);
    buffer.put((byte) leftOverValue);
    buffer.put(payload);
    return buffer.array();
  }
}
