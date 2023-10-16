package protobuf.magic.handler;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Base64Handler extends ByteHandler {
  @Override
  public List<Byte> handle(String str) {
    if (isBase64(str)) {
      byte[] bytes = Base64.getDecoder().decode(str);
      List<Byte> byteList = new ArrayList<>();
      for (byte b : bytes) {
        byteList.add(b);
      }
      return byteList;
    }
    return next.handle(str);
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
