package protobuf.magic;

import java.math.BigInteger;

class BufferReader {
  private final byte[] buffer;
  private int offset;
  private int savedOffset;

  public BufferReader(byte[] buffer) {
    this.buffer = buffer;
    this.offset = 0;
  }

  public BigInteger readVarInt() {
    VarIntResult result = VarintUtils.decodeVarint(buffer, offset);
    offset += result.getLength();
    return result.getValue();
  }

  public byte[] readBuffer(int length) {
    checkByte(length);
    byte[] result = new byte[length];
    System.arraycopy(buffer, offset, result, 0, length);
    offset += length;
    return result;
  }

  public void trySkipGrpcHeader() {
    int backupOffset = offset;

    if (buffer[offset] == 0 && leftBytes() >= 5) {
      offset++;
      int length = readInt32BE(buffer, offset);
      offset += 4;

      if (length > leftBytes()) {
        // Something is wrong, revert
        offset = backupOffset;
      }
    }
  }

  public int leftBytes() {
    return buffer.length - offset;
  }

  private void checkByte(int length) {
    int bytesAvailable = leftBytes();
    if (length > bytesAvailable) {
      throw new RuntimeException(
          "Not enough bytes left. Requested: " + length + " left: " + bytesAvailable);
    }
  }

  public void checkpoint() {
    savedOffset = offset;
  }

  public void resetToCheckpoint() {
    offset = savedOffset;
  }

  private static int readInt32BE(byte[] buffer, int offset) {
    return ((buffer[offset] & 0xFF) << 24)
        | ((buffer[offset + 1] & 0xFF) << 16)
        | ((buffer[offset + 2] & 0xFF) << 8)
        | (buffer[offset + 3] & 0xFF);
  }

  public int getOffset() {
    return offset;
  }
}
