package protobuf.magic.protobuf;

import java.math.BigInteger;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.struct.VarintResult;

public class BufferReader {
  private static final int GRPC_HEADER_FLAG = 0;
  private static final int INT32_BYTE_LENGTH = 4;
  private static final int BYTE_SIZE_BITS = 8;
  private static final int BYTE_MASK = 0xFF;

  private final byte[] buffer;
  private int offset;
  private int savedOffset;

  public BufferReader(byte[] buffer) {
    this.buffer = buffer;
    this.offset = 0;
  }

  public BigInteger readVarInt() {
    VarintResult result = VarintUtils.decodeVarint(buffer, offset);
    offset += result.length();
    return result.value();
  }

  public byte[] readBuffer(int length) throws InsufficientResourcesException {
    checkByte(length);
    byte[] result = new byte[length];
    System.arraycopy(buffer, offset, result, 0, length);
    offset += length;
    return result;
  }

  public void trySkipGrpcHeader() {
    int backupOffset = offset;

    if (buffer.length <= offset) {
      return;
    }
    if (buffer[offset] == GRPC_HEADER_FLAG && leftBytes() >= INT32_BYTE_LENGTH) {
      offset++;
      int length = readInt32BE(buffer, offset);
      offset += INT32_BYTE_LENGTH;

      if (length > leftBytes()) {
        // Something is wrong, revert
        offset = backupOffset;
      }
    }
  }

  public int leftBytes() {
    return buffer.length - offset;
  }

  private void checkByte(int length) throws InsufficientResourcesException {
    int bytesAvailable = leftBytes();
    if (length > bytesAvailable) {
      throw new InsufficientResourcesException(
          "Not enough bytes left. Requested: " + length + " left: " + bytesAvailable);
    }
  }

  public void checkpoint() {
    savedOffset = offset;
  }

  public void resetToCheckpoint() {
    offset = savedOffset;
  }

  /**
   * Reads a 32-bit signed integer from the buffer in big-endian (BE) format.
   *
   * @param buffer The buffer to read the integer from.
   * @param offset The offset from which to start reading in the buffer.
   * @return The 32-bit signed integer read from the buffer in big-endian format.
   */
  private static int readInt32BE(byte[] buffer, int offset) {
    final int shift24 = BYTE_SIZE_BITS * 3;
    final int shift16 = BYTE_SIZE_BITS * 2;
    final int shift8 = BYTE_SIZE_BITS;

    return ((buffer[offset] & BYTE_MASK) << shift24)
        | ((buffer[offset + 1] & BYTE_MASK) << shift16)
        | ((buffer[offset + 2] & BYTE_MASK) << shift8)
        | (buffer[offset + 3] & BYTE_MASK);
  }

  public int getOffset() {
    return offset;
  }
}
