package protobuf.magic;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ProtoDecoderUtils {
  public static Protobuf[] decodeFixed32(byte[] value) {
    Protobuf[] result = new Protobuf[3];

    ByteBuffer bufferFloat = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    float floatValue = bufferFloat.getFloat();
    result[2] = new Protobuf(TYPES.FLOAT, String.format("%.16f", floatValue));
    ByteBuffer bufferInt = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    int intValue = bufferInt.getInt();
    result[0] = new Protobuf(TYPES.INT, String.valueOf(intValue));

    ByteBuffer bufferUint = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    long uintValue = bufferUint.getInt() & 0xffffffffL;
    // Should not return Unsigned Int result when Int is not negative
    if (intValue >= 0) {
      result[1] = new Protobuf(TYPES.UINT, null);
    } else {
      result[1] = new Protobuf(TYPES.UINT, String.valueOf(uintValue));
    }

    return result;
  }

  public static Protobuf[] decodeFixed64(byte[] value) {
    ByteBuffer buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    double floatValue = buffer.getDouble();
    buffer.rewind(); // Reset the buffer position
    long uintValue = buffer.getLong();

    Protobuf[] result = new Protobuf[3];
    result[0] = new Protobuf(TYPES.INT, String.valueOf(uintValue));
    // Check if uintValue is negative. If not, set unsigned integer to null.
    if (uintValue >= 0) {
      result[1] = new Protobuf(TYPES.UINT, null);
    } else {
      result[1] = new Protobuf(TYPES.UINT, Long.toUnsignedString(uintValue));
    }
    result[2] = new Protobuf(TYPES.DOUBLE, String.valueOf(floatValue));

    return result;
  }

  public static Protobuf[] decodeVarintParts(BigInteger value) {
    int rawValue = value.intValue(); // Get the integer value from BigInteger

    // ZigZag decoding
    int signedValue = (rawValue >> 1) ^ (-(rawValue & 1));

    Protobuf[] result = new Protobuf[2];
    result[0] = new Protobuf(TYPES.INT, String.valueOf(rawValue));
    result[1] = new Protobuf(TYPES.SINT, String.valueOf(signedValue));

    return result;
  }

  public static Protobuf decodeStringOrBytes(byte[] value) {
    if (value.length == 0) {
      return new Protobuf(TYPES.STRING_OR_BYTES, "");
    }
    String textValue = new String(value, StandardCharsets.UTF_8);

    // Check if the textValue contains the Unicode replacement character
    if (textValue.contains("\uFFFD")) {
      // Return a Protobuf object with type TYPES.BYTES and value "Byte
      // representation"
      return new Protobuf(TYPES.BYTES, "Byte representation");
    } else {
      return new Protobuf(TYPES.STRING, textValue);
    }
  }
}
