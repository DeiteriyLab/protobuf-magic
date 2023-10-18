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
public enum Type {
  VARINT(0, "sint64"), // int32, int64, uint32, uint64, sint32, sint64, bool, enum
  I64(1, "fixed64"), // fixed64, sfixed64, double
  LEN(2, "string"), // string, bytes, embedded messages, packed repeated fields
  // SGROUP(3, "sgroup"), // group start (deprecated)
  // EGROUP(4, "egroup"), // group end (deprecated)
  I32(5, "sfixed32"); // fixed32, sfixed32, float

  private final int value;
  private final String name;

  Type(int value, String name) {
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
  public static Type fromValue(int value) throws UnknownTypeException {
    for (Type type : Type.values()) {
      if (type.getValue() == value) {
        return type;
      }
    }
    throw new UnknownTypeException(Integer.toString(value));
  }

  public static Type fromName(String name) throws UnknownTypeException {
    for (Type type : Type.values()) {
      if (type.getName().equals(name)) {
        return type;
      }
    }
    throw new UnknownTypeException(name);
  }
}
