package protobuf.magic.struct;

/**
 * ProtobufFieldType enum represents different data types in Protobuf. Each Enum constant is
 * associated with an int value and a string name. The int value represents the field number
 * associated with the type in a Protobuf message. The String name represents the name of the data
 * type. This enum provides a static method {@link #fromValue(int)} to convert an int value to
 * corresponding enum type.
 *
 * <p>In case of expanding functionality and adding extra behaviour for specific types, it might be
 * reasonable to split this Enum into two separate ones - for varint types and non-varint types, It
 * helps to maintain the codebase and increases readability.
 */
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

  /**
   * Returns the enum constant of this type with the specified value.
   *
   * @param value the field number as defined in the Protobuf message
   * @return the enum constant with the specified value or {@code null} if no ProtobufFieldType has
   *     this value
   */
  public static ProtobufFieldType fromValue(int value) {
    for (ProtobufFieldType type : ProtobufFieldType.values()) {
      if (type.getValue() == value) {
        return type;
      }
    }
    return null;
  }
}
