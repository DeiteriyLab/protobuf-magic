package protobuf.magic.protobuf;

import java.math.BigInteger;
import protobuf.magic.struct.VarintResult;

public class VarintUtils {
  private static final BigInteger BIGINT_1 = BigInteger.ONE;
  private static final BigInteger BIGINT_2 = BigInteger.valueOf(2);

  public static BigInteger interpretAsSignedType(BigInteger n) {
    boolean isEven = n.and(BigInteger.ONE).equals(BigInteger.ZERO);
    if (isEven) {
      return n.divide(BIGINT_2);
    } else {
      return n.add(BIGINT_1).divide(BIGINT_2).multiply(BigInteger.valueOf(-1));
    }
  }

  public static VarintResult decodeVarint(byte[] buffer, int offset) {
    BigInteger res = BigInteger.ZERO;
    int shift = 0;
    int byteValue;

    do {
      if (offset >= buffer.length) {
        throw new IndexOutOfBoundsException("Index out of bound decoding varint");
      }

      byteValue = buffer[offset++] & 0xFF;

      BigInteger multiplier = BIGINT_2.pow(shift);
      BigInteger thisByteValue = BigInteger.valueOf(byteValue & 0x7F).multiply(multiplier);
      shift += 7;
      res = res.add(thisByteValue);
    } while (byteValue >= 0x80);

    return new VarintResult(res, shift / 7);
  }
}
