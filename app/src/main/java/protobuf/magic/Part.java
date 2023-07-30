package protobuf.magic;

import java.util.Arrays;

public class Part {
  private int[] byteRange;
  private int index;
  private Protobuf protobuf;

  Part(int[] byteRange, int index, Protobuf protobuf) {
    this.byteRange = byteRange;
    this.index = index;
    this.protobuf = protobuf;
  }

  public int[] getByteRange() {
    return byteRange;
  }

  public int getIndex() {
    return index;
  }

  public Protobuf getProtobuf() {
    return protobuf;
  }

  public TYPES getType() {
    return protobuf.getType();
  }

  public String getValue() {
    return protobuf.getValue();
  }

  @Override
  public String toString() {
    return "Part{"
        + "byteRange="
        + Arrays.toString(byteRange)
        + ", index="
        + index
        + ", protobuf="
        + protobuf
        + "}";
  }
}
