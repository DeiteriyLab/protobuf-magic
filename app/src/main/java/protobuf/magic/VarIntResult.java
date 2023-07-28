package protobuf.magic;

import java.math.BigInteger;

public class VarIntResult {
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
