package protobuf.magic;

import java.math.BigInteger;

public class Part {
  int[] byteRange;
  BigInteger index;
  BigInteger type;
  Object value;

  Part(int[] byteRange, BigInteger index, BigInteger type, Object value) {
    this.byteRange = byteRange;
    this.index = index;
    this.type = type;
    this.value = value;
  }
}
