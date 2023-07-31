package protobuf.magic.decode;

import java.nio.ByteBuffer;
import protobuf.magic.struct.ProtobufFieldValue;

public interface DecodingStrategy {
  Object decode(ByteBuffer buffer);

  ProtobufFieldValue createValue(Object value);

  default int getRequiredBytes() {
    return 4;
  }
}
