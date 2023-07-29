package protobuf.magic;

public class Proto {
  final String type;
  final String value;

  public Proto(String type, String value) {
    this.type = type;
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public String getValue() {
    return value;
  }
}
