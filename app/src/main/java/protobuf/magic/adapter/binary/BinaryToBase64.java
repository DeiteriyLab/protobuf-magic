package protobuf.magic.adapter.binary;

import java.util.Base64;
import java.util.List;

public class BinaryToBase64 implements BinaryToString {
  @Override
  public String convert(List<Byte> bytes) {
    return Base64.getEncoder().encodeToString(toArray(bytes));
  }

  private byte[] toArray(List<Byte> bufferList) {
    byte[] buffer = new byte[bufferList.size()];
    for (int i = 0; i < bufferList.size(); i++) {
      buffer[i] = bufferList.get(i);
    }
    return buffer;
  }
}
