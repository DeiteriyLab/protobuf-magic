package protobuf.magic.decode;

import java.nio.ByteBuffer;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class Fixed64DecodingStrategy implements DecodingStrategy {
  @Override
  public Object decode(ByteBuffer buffer) {
    return buffer.getLong();
  }

  @Override
  public ProtobufFieldValue createValue(Object value) {
    long uintValue = (long) value;
    if (uintValue >= 0) {
      return new ProtobufFieldValue(ProtobufFieldType.UINT, null);
    } else {
      return new ProtobufFieldValue(ProtobufFieldType.UINT, Long.toUnsignedString(uintValue));
    }
  }
}
