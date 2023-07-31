package protobuf.magic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import protobuf.magic.decode.DecodingStrategy;
import protobuf.magic.decode.Fixed32DecodingStrategy;
import protobuf.magic.decode.Fixed64DecodingStrategy;
import protobuf.magic.decode.StringOrBytesDecodingStrategy;
import protobuf.magic.decode.VarintDecodingStrategy;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtoDecoderUtils {
  private static final DecodingStrategy fixed32DecodingStrategy = new Fixed32DecodingStrategy();
  private static final DecodingStrategy fixed64DecodingStrategy = new Fixed64DecodingStrategy();
  private static final DecodingStrategy varintDecodingStrategy = new VarintDecodingStrategy();
  private static final DecodingStrategy stringOrBytesDecodingStrategy =
      new StringOrBytesDecodingStrategy();

  public static ProtobufFieldValue[] decodeFixed32(byte[] value) {
    ByteBuffer buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    Object decoded = fixed32DecodingStrategy.decode(buffer);
    ProtobufFieldValue fieldValue = fixed32DecodingStrategy.createValue(decoded);
    return new ProtobufFieldValue[] {fieldValue};
  }

  public static ProtobufFieldValue[] decodeFixed64(byte[] value) {
    ByteBuffer buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    Object decoded = fixed64DecodingStrategy.decode(buffer);
    ProtobufFieldValue fieldValue = fixed64DecodingStrategy.createValue(decoded);
    return new ProtobufFieldValue[] {fieldValue};
  }

  public static ProtobufFieldValue[] decodeVarintParts(byte[] value) {
    ByteBuffer buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    Object decoded = varintDecodingStrategy.decode(buffer);
    ProtobufFieldValue fieldValue = varintDecodingStrategy.createValue(decoded);
    return new ProtobufFieldValue[] {fieldValue};
  }

  public static ProtobufFieldValue decodeStringOrBytes(byte[] value) {
    ByteBuffer buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    Object decoded = stringOrBytesDecodingStrategy.decode(buffer);
    ProtobufFieldValue fieldValue = stringOrBytesDecodingStrategy.createValue(decoded);
    return fieldValue;
  }
}
