package protobuf.magic.struct;

import java.util.Arrays;

public class ProtobufField {
  private final int[] byteRange;
  private final int index;
  private final ProtobufFieldValue protobuf;

  public ProtobufField(int[] byteRange, int index, ProtobufFieldValue protobuf) {
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

  public ProtobufFieldValue getProtobuf() {
    return protobuf;
  }

  public ProtobufFieldType getType() {
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
