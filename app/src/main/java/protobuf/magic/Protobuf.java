package protobuf.magic;

public class Protobuf {
  final TYPES type;
  final String value;

  public Protobuf(TYPES type, String value) {
    this.type = type;
    this.value = value;
  }

  public TYPES getType() {
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
