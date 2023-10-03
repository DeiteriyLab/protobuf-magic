package protobuf.magic.struct;

public class ProtobufFieldValue {
  private final ProtobufFieldType type;
  private final byte[] value;

  public ProtobufFieldValue(ProtobufFieldType type, byte[] value) {
    this.type = type;
    this.value = value;
  }

  public ProtobufFieldType getType() {
    return type;
  }

  public byte[] getValue() {
    return value;
  }
}
