package protobuf.magic;

enum TYPES {
  VARINT(0, "varint"),
  FIXED64(1, "fixed64"),
  LENDELIM(2, "len_delim"),
  FIXED32(5, "fixed32");

  private final int value;
  private final String name;

  TYPES(int value, String name) {
    this.value = value;
    this.name = name;
  }

  public int getValue() {
    return value;
  }

  public String getName() {
    return name;
  }
}
