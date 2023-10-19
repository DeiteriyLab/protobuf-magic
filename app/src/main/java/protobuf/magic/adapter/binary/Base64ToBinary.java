package protobuf.magic.adapter.binary;

import java.util.ArrayList;
import java.util.List;
import protobuf.magic.exception.*;

public class Base64ToBinary implements StringToBinary {
  @Override
  public List<Byte> convert(String str) throws UnknownStructException {
    if (!isBase64(str)) {
      throw new UnknownStructException("Not a base64 string");
    }
    return decodeBase64(str);
  }

  private boolean isBase64(String str) {
    try {
      decodeBase64(str);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private static List<Byte> decodeBase64(String base64) {
    String base64Characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    List<Byte> decodedBytes = new ArrayList<>();

    for (int i = 0; i < base64.length(); i += 4) {
      int[] quad = new int[4];
      for (int j = 0; j < 4; j++) {
        int c = base64Characters.indexOf(base64.charAt(i + j));
        if (c != -1) {
          quad[j] = c;
        }
      }

      int firstByte = (quad[0] << 2) | (quad[1] >> 4);
      int secondByte = ((quad[1] & 0x0F) << 4) | (quad[2] >> 2);
      int thirdByte = ((quad[2] & 0x03) << 6) | quad[3];

      decodedBytes.add((byte) firstByte);
      if (base64.charAt(i + 2) != '=') {
        decodedBytes.add((byte) secondByte);
      }
      if (base64.charAt(i + 3) != '=') {
        decodedBytes.add((byte) thirdByte);
      }
    }

    return decodedBytes;
  }
}
