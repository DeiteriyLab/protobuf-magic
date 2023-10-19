package protobuf.magic.adapter.binary;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.CustomLog;
import protobuf.magic.exception.*;

@CustomLog
public class HexToBinary implements StringToBinary {
  private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");

  @Override
  public List<Byte> convert(String str) throws UnknownStructException {
    String normalizedHexInput = sanitizeHexInput(str);

    if (isValidHex(normalizedHexInput)) {
      byte[] bytes = hexStringToByteArray(normalizedHexInput);
      return List.copyOf(bytesToList(bytes));
    }
    throw new UnknownStructException("Not a hex string");
  }

  private static String sanitizeHexInput(String input) {
    return input.replaceAll("\\s|0x", "").toLowerCase();
  }

  private static boolean isValidHex(String str) {
    return HEX_PATTERN.matcher(str).matches();
  }

  private static byte[] hexStringToByteArray(String hexString) {
    int len = hexString.length();
    byte[] data = new byte[len / 2];

    for (int i = 0; i < len; i += 2) {
      try {
        data[i / 2] =
            (byte)
                ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
      } catch (StringIndexOutOfBoundsException e) {
        log.error(e);
      }
    }

    return data;
  }

  private static List<Byte> bytesToList(byte[] bytes) {
    List<Byte> byteList = new ArrayList<>();
    for (byte b : bytes) {
      byteList.add(b);
    }
    return byteList;
  }
}
