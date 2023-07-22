package protobuf.magic;

import java.math.BigInteger;

public class VarintUtils {
  private static final BigInteger BIGINT_1 = BigInteger.ONE;
  private static final BigInteger BIGINT_2 = BigInteger.valueOf(2);

  public static BigInteger interpretAsSignedType(BigInteger n) {
    boolean isEven = n.testBit(0);
    if (isEven) {
      return n.divide(BIGINT_2);
    } else {
      return BIGINT_2.multiply(n.add(BIGINT_1)).divide(BIGINT_2).negate();
    }
  }

  public static VarIntResult decodeVarint(byte[] buffer, int offset) {
    BigInteger res = BigInteger.ZERO;
    int shift = 0;
    int b;

    do {
      if (offset >= buffer.length) {
        throw new IndexOutOfBoundsException("Index out of bounds decoding varint");
      }

      b = buffer[offset++];
      BigInteger multiplier = BIGINT_2.pow(shift);
      BigInteger thisByteValue = BigInteger.valueOf(b & 0x7F).multiply(multiplier);
      shift += 7;
      res = res.add(thisByteValue);
    } while (b >= 0x80);

    return new VarIntResult(res, shift / 7);
  }

  public static class VarIntResult {
    private final BigInteger value;
    private final int length;

    public VarIntResult(BigInteger value, int length) {
      this.value = value;
      this.length = length;
    }

    public BigInteger getValue() {
      return value;
    }

    public int getLength() {
      return length;
    }
  }
}
