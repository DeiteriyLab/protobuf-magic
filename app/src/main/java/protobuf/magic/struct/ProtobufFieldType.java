package protobuf.magic.struct;

public enum ProtobufFieldType {
  VARINT(0, "varint"),
  FIXED64(1, "fixed64"),
  LENDELIM(2, "len_delim"),
  FIXED32(5, "fixed32"),
  UINT(6, "uint"),
  FLOAT(7, "float"),
  DOUBLE(8, "double"),
  INT(9, "int"),
  SINT(10, "sint"),
  BYTES(11, "bytes"),
  STRING(12, "string"),
  STRING_OR_BYTES(13, "string_or_bytes");

  private final int value;
  private final String name;

  ProtobufFieldType(int value, String name) {
    this.value = value;
    this.name = name;
  }

  public int getValue() {
    return value;
  }

  public String getName() {
    return name;
  }

  public static ProtobufFieldType fromValue(int value) {
    for (ProtobufFieldType type : ProtobufFieldType.values()) {
      if (type.getValue() == value) {
        return type;
      }
    }
    return null;
  }
}
