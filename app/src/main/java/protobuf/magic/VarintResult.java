package protobuf.magic;

import java.math.BigInteger;

public class VarintResult {
  private final BigInteger value;
  private final int length;

  public VarintResult(BigInteger value, int length) {
    this.value = value;
    this.length = length;
  }

  public BigInteger getValue() {
    return value;
  }

  public int getLength() {
    return length;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    VarintResult that = (VarintResult) o;
    return value.equals(that.value) && length == that.length;
  }
}
