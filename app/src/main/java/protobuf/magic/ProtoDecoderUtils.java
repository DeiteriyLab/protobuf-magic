package protobuf.magic;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtoDecoderUtils {
  public static ProtobufFieldValue[] decodeFixed32(byte[] value) {
    ProtobufFieldValue[] result = new ProtobufFieldValue[3];

    ByteBuffer bufferFloat = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    float floatValue = bufferFloat.getFloat();
    result[2] = new ProtobufFieldValue(ProtobufFieldType.FLOAT, String.format("%.16f", floatValue));
    ByteBuffer bufferInt = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    int intValue = bufferInt.getInt();
    result[0] = new ProtobufFieldValue(ProtobufFieldType.INT, String.valueOf(intValue));

    ByteBuffer bufferUint = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    long uintValue = bufferUint.getInt() & 0xffffffffL;
    // Should not return Unsigned Int result when Int is not negative
    if (intValue >= 0) {
      result[1] = new ProtobufFieldValue(ProtobufFieldType.UINT, null);
    } else {
      result[1] = new ProtobufFieldValue(ProtobufFieldType.UINT, String.valueOf(uintValue));
    }

    return result;
  }

  public static ProtobufFieldValue[] decodeFixed64(byte[] value) {
    ByteBuffer buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    double floatValue = buffer.getDouble();
    buffer.rewind(); // Reset the buffer position
    long uintValue = buffer.getLong();

    ProtobufFieldValue[] result = new ProtobufFieldValue[3];
    result[0] = new ProtobufFieldValue(ProtobufFieldType.INT, String.valueOf(uintValue));
    // Check if uintValue is negative. If not, set unsigned integer to null.
    if (uintValue >= 0) {
      result[1] = new ProtobufFieldValue(ProtobufFieldType.UINT, null);
    } else {
      result[1] = new ProtobufFieldValue(ProtobufFieldType.UINT, Long.toUnsignedString(uintValue));
    }
    result[2] = new ProtobufFieldValue(ProtobufFieldType.DOUBLE, String.valueOf(floatValue));

    return result;
  }

  public static ProtobufFieldValue[] decodeVarintParts(BigInteger value) {
    int rawValue = value.intValue(); // Get the integer value from BigInteger

    // ZigZag decoding
    int signedValue = (rawValue >> 1) ^ (-(rawValue & 1));

    ProtobufFieldValue[] result = new ProtobufFieldValue[2];
    result[0] = new ProtobufFieldValue(ProtobufFieldType.INT, String.valueOf(rawValue));
    result[1] = new ProtobufFieldValue(ProtobufFieldType.SINT, String.valueOf(signedValue));

    return result;
  }

  public static ProtobufFieldValue decodeStringOrBytes(byte[] value) {
    if (value.length == 0) {
      return new ProtobufFieldValue(ProtobufFieldType.STRING_OR_BYTES, "");
    }
    String textValue = new String(value, StandardCharsets.UTF_8);

    // Check if the textValue contains the Unicode replacement character
    if (textValue.contains("\uFFFD")) {
      // Return a Protobuf object with type TYPES.BYTES and value "Byte
      // representation"
      return new ProtobufFieldValue(ProtobufFieldType.BYTES, "Byte representation");
    } else {
      return new ProtobufFieldValue(ProtobufFieldType.STRING, textValue);
    }
  }
}
