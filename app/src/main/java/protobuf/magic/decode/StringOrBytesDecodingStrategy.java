package protobuf.magic.decode;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class StringOrBytesDecodingStrategy implements DecodingStrategy {
  @Override
  public Object decode(ByteBuffer buffer) {
    byte[] bytes = new byte[buffer.remaining()];
    buffer.get(bytes);
    return bytes;
  }

  @Override
  public ProtobufFieldValue createValue(Object value) {
    byte[] bytes = (byte[]) value;
    String textValue = new String(bytes, StandardCharsets.UTF_8);
    if (textValue.contains("\uFFFD")) {
      return new ProtobufFieldValue(ProtobufFieldType.BYTES, "Byte representation");
    } else {
      return new ProtobufFieldValue(ProtobufFieldType.STRING, textValue);
    }
  }
}
