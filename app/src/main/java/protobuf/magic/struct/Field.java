package protobuf.magic.struct;

public record Field(int index, Type type, Node value, ByteRange byterange) {
  public Object parseValue() {
    return (Object)
        switch (type) {
          case I32 -> value.asInteger();
          case I64 -> value.asLong();
          case VARINT -> value.asLong();
          case LEN -> value.asString();
          default -> value.asBytes();
        };
  }
}
