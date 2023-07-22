package protobuf.magic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BufferUtils {
  public static byte[] parseInput(String input) {
    String normalizedInput = input.replaceAll("\\s", "");
    String normalizedHexInput = normalizedInput.replaceAll("0x", "").toLowerCase();
    if (isHex(normalizedHexInput)) {
      return hexStringToByteArray(normalizedHexInput);
    } else {
      return java.util.Base64.getDecoder().decode(normalizedInput);
    }
  }

  public static boolean isHex(String str) {
    Pattern pattern = Pattern.compile("^[0-9a-fA-F]+$");
    Matcher matcher = pattern.matcher(str);
    return matcher.matches();
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

  public static String bufferLeToBeHex(byte[] buffer) {
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

  public static void main(String[] args) {
    String input = "0x48656c6c6f20576f726c64";
    byte[] buffer = parseInput(input);
    System.out.println(bufferToPrettyHex(buffer)); // Output: "48 65 6c 6c 6f 20 57 6f 72 6c 64"
    System.out.println(bufferLeToBeHex(buffer)); // Output: "646c726f20576f72"
  }
}
