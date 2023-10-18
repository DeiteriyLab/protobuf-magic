package protobuf.magic.struct;

public record Field(int index, Type type, Node value, ByteRange byterange) {
  public Object parseValue() {
    return (Object)
        switch (type) {
          case I32 -> value.asString().getBytes();
          case I64 -> value.asString().getBytes();
          case VARINT -> value.asLong();
          case LEN -> value.asString();
          default -> value.asBytes();
        };
  }
}
