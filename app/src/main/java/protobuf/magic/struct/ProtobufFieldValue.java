package protobuf.magic.struct;

public class ProtobufFieldValue {
  private final ProtobufFieldType type;
  private final String value;

  public ProtobufFieldValue(ProtobufFieldType type, String value) {
    this.type = type;
    this.value = value;
  }

  public ProtobufFieldType getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "Protobuf{" + "type=" + type + ", value='" + value + '\'' + "}";
  }
}
