package protobuf.magic.struct;

import java.util.ArrayList;
import java.util.List;
import protobuf.magic.adapter.importer.BinaryToProtobuf;
import protobuf.magic.exception.UnknownStructException;

public class Node {
  private final byte[] value;

  public Node(byte[] value) {
    this.value = value;
  }

  public Long asLong() {
    try {
      return Long.valueOf(new String(value));
    } catch (NumberFormatException e) {
      return 0L;
    }
  }

  public String asString() {
    return new String(value);
  }

  public byte[] asBytes() {
    return new String(value).getBytes();
  }

  public DynamicProtobuf asProtobuf() throws UnknownStructException {
    BinaryToProtobuf binaryToProtobuf = new BinaryToProtobuf();
    List<Byte> bytes = new ArrayList<>();
    for (byte b : asBytes()) {
      bytes.add(b);
    }
    return binaryToProtobuf.convert(bytes);
  }

  public Object asObject() {
    return value;
  }
}
