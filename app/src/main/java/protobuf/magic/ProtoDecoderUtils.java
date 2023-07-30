package protobuf.magic;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class ProtoDecoderUtils {
  public static Proto[] decodeFixed32(byte[] value) {
    Proto[] result = new Proto[3];

    ByteBuffer bufferFloat = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    float floatValue = bufferFloat.getFloat();
    result[2] = new Proto("Float", String.format("%.16f", floatValue));
    ByteBuffer bufferInt = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    int intValue = bufferInt.getInt();
    result[0] = new Proto("Int", String.valueOf(intValue));

    ByteBuffer bufferUint = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    long uintValue = bufferUint.getInt() & 0xffffffffL;
    // Should not return Unsigned Int result when Int is not negative
    if (intValue >= 0) {
      result[1] = new Proto("Unsigned Int", null);
    } else {
      result[1] = new Proto("Unsigned Int", String.valueOf(uintValue));
    }

    return result;
  }

  public static Proto[] decodeFixed64(byte[] value) {
    ByteBuffer buffer = ByteBuffer.wrap(value).order(ByteOrder.LITTLE_ENDIAN);
    double floatValue = buffer.getDouble();
    buffer.rewind(); // Reset the buffer position
    long uintValue = buffer.getLong();

    Proto[] result = new Proto[3];
    result[0] = new Proto("Int", String.valueOf(uintValue));
    // Check if uintValue is negative. If not, set unsigned integer to null.
    if (uintValue >= 0) {
      result[1] = new Proto("Unsigned Int", null);
    } else {
      result[1] = new Proto("Unsigned Int", Long.toUnsignedString(uintValue));
    }
    result[2] = new Proto("Double", String.valueOf(floatValue));

    return result;
  }

  public static Proto[] decodeVarintParts(BigInteger value) {
    int rawValue = value.intValue(); // Get the integer value from BigInteger

    // ZigZag decoding
    int signedValue = (rawValue >> 1) ^ (-(rawValue & 1));

    Proto[] result = new Proto[2];
    result[0] = new Proto("Int", String.valueOf(rawValue));
    result[1] = new Proto("Signed Int", String.valueOf(signedValue));

    return result;
  }

  public static Proto decodeStringOrBytes(byte[] value) {
    if (value.length == 0) {
      return new Proto("String|Bytes", "");
    }
    String textValue = new String(value, StandardCharsets.UTF_8);

    // Check if the textValue contains the Unicode replacement character
    if (textValue.contains("\uFFFD")) {
      // Return a Proto object with type "Bytes" and value "Byte representation"
      return new Proto("Bytes", "Byte representation");
    } else {
      return new Proto("String", textValue);
    }
  }
}
