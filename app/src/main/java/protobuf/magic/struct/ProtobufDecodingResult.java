package protobuf.magic.struct;

import java.util.List;

public class ProtobufDecodingResult {
  private final List<ProtobufField> protobufFields;
  private final byte[] leftOver;

  public ProtobufDecodingResult(List<ProtobufField> protobufFields, byte[] leftOver) {
    this.protobufFields = protobufFields;
    this.leftOver = leftOver;
  }

  public List<ProtobufField> getProtobufFields() {
    return protobufFields;
  }

  public byte[] getLeftOver() {
    return leftOver;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("DecodeResult{");
    sb.append("parts=");
    if (protobufFields != null) {
      for (ProtobufField part : protobufFields) {
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
