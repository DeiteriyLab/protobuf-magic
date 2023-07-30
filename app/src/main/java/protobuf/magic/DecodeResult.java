package protobuf.magic;

import java.util.List;

public class DecodeResult {
  List<Part> parts;
  byte[] leftOver;

  DecodeResult(List<Part> parts, byte[] leftOver) {
    this.parts = parts;
    this.leftOver = leftOver;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("DecodeResult{");
    sb.append("parts=");
    if (parts != null) {
      for (Part part : parts) {
        sb.append(part.toString()).append(", ");
      }
    }
    sb.append("leftOver=");
    if (leftOver != null) {
      for (byte b : leftOver) {
        sb.append(String.format("%02X ", b));
      }
    }
    sb.append("}");
    return sb.toString();
  }
}
