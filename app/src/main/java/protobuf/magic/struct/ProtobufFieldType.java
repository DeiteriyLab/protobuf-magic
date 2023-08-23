package protobuf.magic.struct;

import protobuf.magic.exception.UnknownTypeException;

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
  VARINT(0, "VARINT"),
  FIXED64(1, "FIXED64"),
  LENDELIM(2, "LENDELIM"),
  FIXED32(5, "FIXED32"),
  UINT(6, "UNIT"),
  FLOAT(7, "FLOAT"),
  DOUBLE(8, "DOUBLE"),
  INT(9, "INT"),
  SINT(10, "SINT"),
  BYTES(11, "BYTES"),
  STRING(12, "STRING"),
  STRING_OR_BYTES(13, "STRING_OR_BYTES");

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
   * @throws UnknownTypeException
   */
  public static ProtobufFieldType fromValue(int value) throws UnknownTypeException {
    for (ProtobufFieldType type : ProtobufFieldType.values()) {
      if (type.getValue() == value) {
        return type;
      }
    }
    throw new UnknownTypeException("Unknown ProtobufFieldType: " + value);
  }

  public static ProtobufFieldType fromName(String name) throws UnknownTypeException {
    for (ProtobufFieldType type : ProtobufFieldType.values()) {
      if (type.getName().equals(name)) {
        return type;
      }
    }
    throw new UnknownTypeException("Unknown ProtobufFieldType: " + name);
  }
}
