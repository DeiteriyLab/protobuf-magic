package protobuf.magic.protobuf;

import protobuf.magic.struct.VarintResult;

public class VarintUtils {

  public static long interpretAsSignedType(long n) {
    boolean isEven = (n & 1) == 0;
    if (isEven) {
      return n / 2;
    } else {
      return (n + 1) / 2 * -1;
    }
  }

  public static VarintResult decodeVarint(byte[] buffer, int offset) {
    long res = 0L;
    int shift = 0;
    int byteValue;

    do {
      if (offset >= buffer.length) {
        throw new IndexOutOfBoundsException("Index out of bound decoding varint");
      }

      byteValue = buffer[offset++] & 0xFF;

      long thisByteValue = (long) (byteValue & 0x7F) << shift;
      shift += 7;
      res |= thisByteValue;
    } while (byteValue >= 0x80);

    return new VarintResult(res, shift / 7);
  }
}
