package protobuf.magic;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

public class EncodingUtils {
  private static final Pattern patternHex = Pattern.compile("^[0-9a-fA-F]+$");

  public static byte[] parseInput(String input) {
    String normalizedInput = input.replaceAll("\\s", "");
    String normalizedHexInput = normalizedInput.replaceAll("0x", "").toLowerCase();
    if (isHex(normalizedHexInput)) {
      return hexStringToByteArray(normalizedHexInput);
    } else if (isBase64(normalizedInput)) {
      return Base64.getDecoder().decode(normalizedInput);
    }
    return rawStringToBytes(input);
  }

  public static boolean isHex(String str) {
    return patternHex.matcher(str).matches();
  }

  public static byte[] rawStringToBytes(String str) {
    return str.getBytes(StandardCharsets.UTF_8);
  }

  public static boolean isBase64(String str) {
    try {
      Base64.getDecoder().decode(str);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  public static String bufferToPrettyHex(byte[] buffer) {
    StringBuilder output = new StringBuilder();
    for (byte b : buffer) {
      if (output.length() > 0) {
        output.append(" ");
      }

      String hex = String.format("%02x", b);
      output.append(hex);
    }
    return output.toString();
  }

  public static String bufferToHex(byte[] buffer) {
    StringBuilder output = new StringBuilder();
    for (int i = buffer.length - 1; i >= 0; i--) {
      String hex = String.format("%02x", buffer[i]);
      output.append(hex);
    }
    return output.toString();
  }

  private static byte[] hexStringToByteArray(String hexString) {
    int len = hexString.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] =
          (byte)
              ((Character.digit(hexString.charAt(i), 16) << 4)
                  + Character.digit(hexString.charAt(i + 1), 16));
    }
    return data;
  }
}
