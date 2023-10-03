package protobuf.magic.struct;

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

  public byte[] getValue() {
    return protobuf.getValue();
  }
}
