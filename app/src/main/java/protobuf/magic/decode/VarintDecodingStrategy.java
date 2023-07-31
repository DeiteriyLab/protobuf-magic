package protobuf.magic.decode;

import java.nio.ByteBuffer;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class VarintDecodingStrategy implements DecodingStrategy {
  @Override
  public Object decode(ByteBuffer buffer) {
    int rawValue = buffer.getInt();
    int signedValue = (rawValue >> 1) ^ (-(rawValue & 1));
    return signedValue;
  }

  @Override
  public ProtobufFieldValue createValue(Object value) {
    int signedValue = (int) value;
    return new ProtobufFieldValue(ProtobufFieldType.SINT, String.valueOf(signedValue));
  }
}
