package protobuf.magic;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ProtoDecoderUtils {
  public static String decodeFixed32(byte[] value) {
    int intValue = ByteBuffer.wrap(value).getInt();
    long uintValue = intValue & 0xFFFFFFFFL; // Convert signed int to unsigned long
    float floatValue = ByteBuffer.wrap(value).getFloat();

    StringBuilder result = new StringBuilder();

    result.append("{ type: \"Int\", value: ").append(intValue).append(" }");

    if (intValue != uintValue) {
      result.append(", { type: \"Unsigned Int\", value: ").append(uintValue).append(" }");
    }

    result.append(", { type: \"Float\", value: ").append(floatValue).append(" }");

    return result.toString();
  }

  public static String decodeFixed64(byte[] value) {
    double floatValue = ByteBuffer.wrap(value).getDouble();
    BigInteger uintValue = new BigInteger(bufferLeToBeHex(value), 16);
    BigInteger intValue = twoComplements(uintValue);

    StringBuilder result = new StringBuilder();

    result.append("{ type: \"Int\", value: ").append(intValue).append(" }");

    if (!intValue.equals(uintValue)) {
      result.append(", { type: \"Unsigned Int\", value: ").append(uintValue).append(" }");
    }

    result.append(", { type: \"Double\", value: ").append(floatValue).append(" }");

    return result.toString();
  }

  public static String decodeVarintParts(String value) {
    BigInteger intVal = new BigInteger(value);
    StringBuilder result = new StringBuilder();

    result.append("{ type: \"Int\", value: ").append(intVal).append(" }");

    BigInteger signedIntVal = interpretAsSignedType(intVal);
    if (!signedIntVal.equals(intVal)) {
      result.append(", { type: \"Signed Int\", value: ").append(signedIntVal).append(" }");
    }

    return result.toString();
  }

  public static String decodeStringOrBytes(byte[] value) {
    if (value.length == 0) {
      return "{ type: \"string|bytes\", value: \"\" }";
    }

    try {
      String decodedString = new String(value, StandardCharsets.UTF_8);
      return "{ type: \"string\", value: \"" + decodedString + "\" }";
    } catch (Exception e) {
      String prettyHex = bufferToPrettyHex(value);
      return "{ type: \"bytes\", value: \"" + prettyHex + "\" }";
    }
  }

  private static String bufferToPrettyHex(byte[] buffer) {
    StringBuilder output = new StringBuilder();
    for (byte v : buffer) {
      if (output.length() > 0) {
        output.append(" ");
      }

      String hex = String.format("%02x", v & 0xFF);
      output.append(hex);
    }
    return output.toString();
  }

  private static String bufferLeToBeHex(byte[] buffer) {
    StringBuilder output = new StringBuilder();
    for (int i = buffer.length - 1; i >= 0; i--) {
      String hex = String.format("%02x", buffer[i] & 0xFF);
      output.append(hex);
    }
    return output.toString();
  }

  private static BigInteger twoComplements(BigInteger uintValue) {
    BigInteger maxLong = new BigInteger("7fffffffffffffff", 16);
    BigInteger longForComplement = new BigInteger("10000000000000000", 16);

    if (uintValue.compareTo(maxLong) > 0) {
      return uintValue.subtract(longForComplement);
    } else {
      return uintValue;
    }
  }

  private static BigInteger interpretAsSignedType(BigInteger value) {
    BigInteger maxLong = new BigInteger("7fffffffffffffff", 16);

    if (value.compareTo(maxLong) > 0) {
      BigInteger mask = new BigInteger("FFFFFFFFFFFFFFFF", 16);
      return value.and(mask);
    } else {
      return value;
    }
  }
}
