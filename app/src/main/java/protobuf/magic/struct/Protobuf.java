package protobuf.magic.struct;

import java.util.List;

public class Protobuf {
  private final List<ProtobufField> protobufFields;
  private final byte[] leftOver;
  private final int lenLeftOver;

  public Protobuf(List<ProtobufField> protobufFields, byte[] leftOver, int lenLeftOver) {
    this.protobufFields = protobufFields;
    this.leftOver = leftOver;
    this.lenLeftOver = lenLeftOver;
  }

  public int getLenLeftOver() {
    return lenLeftOver;
  }

  public List<ProtobufField> getProtobufFields() {
    return protobufFields;
  }

  public byte[] getLeftOver() {
    return leftOver;
  }
}
