package protobuf.magic.adapter.binary;

import java.util.ArrayList;
import java.util.Base64;
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

  private List<Byte> decodeBase64(String str) {
    byte[] bytes = Base64.getDecoder().decode(str);
    List<Byte> byteList = new ArrayList<>();
    for (byte b : bytes) {
      byteList.add(b);
    }
    return byteList;
  }

  private boolean isBase64(String str) {
    try {
      Base64.getDecoder().decode(str);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
