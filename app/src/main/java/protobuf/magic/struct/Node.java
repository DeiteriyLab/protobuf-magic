package protobuf.magic.struct;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import lombok.CustomLog;
import protobuf.magic.adapter.importer.BinaryToProtobuf;
import protobuf.magic.exception.UnknownStructException;

@CustomLog
public class Node {
  private final byte[] value;

  public Node(byte[] value) {
    List<Byte> list = new ArrayList<>();
    for (byte b : value) {
      list.add(b);
    }
    log.debug("Constructed Node: " + list);
    this.value = value;
  }

  public Long asLong() {
    try {
      return ByteBuffer.wrap(value).getLong();
    } catch (BufferUnderflowException | BufferOverflowException e) {
      return 0L;
    }
  }

  public Double asDouble() {
    try {
      return Double.valueOf(new String(value));
    } catch (NumberFormatException e) {
      return 0.0d;
    }
  }

  public Float asFloat() {
    try {
      return Float.valueOf(new String(value));
    } catch (NumberFormatException e) {
      return 0.0f;
    }
  }

  public Integer asInteger() {
    try {
      return Integer.valueOf(new String(value));
    } catch (NumberFormatException e) {
      return 0;
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
