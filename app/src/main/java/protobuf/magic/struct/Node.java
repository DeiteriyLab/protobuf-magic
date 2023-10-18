package protobuf.magic.struct;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import lombok.CustomLog;
import lombok.ToString;
import protobuf.magic.adapter.importer.BinaryToProtobuf;
import protobuf.magic.exception.UnknownStructException;

@CustomLog
@ToString
public class Node {
  private byte[] value;

  public Node(byte[] value) {
    this.value = value;
  }

  public double asDouble() {
    try {
      return ByteBuffer.wrap(value).getDouble();
    } catch (BufferUnderflowException | BufferOverflowException e) {
      log.error(e);
      return 0.0d;
    }
  }

  public void setDouble(double value) {
    this.value = ByteBuffer.allocate(8).putDouble(value).array();
  }

  public float asFloat() {
    try {
      return ByteBuffer.wrap(value).getFloat();
    } catch (BufferUnderflowException | BufferOverflowException e) {
      log.error(e);
      return 0.0f;
    }
  }

  public void setFloat(float value) {
    this.value = ByteBuffer.allocate(4).putFloat(value).array();
  }

  public Long asLong() {
    try {
      return ByteBuffer.wrap(value).getLong();
    } catch (BufferUnderflowException | BufferOverflowException e) {
      log.error(e);
      return 0L;
    }
  }

  public void setLong(Long value) {
    this.value = ByteBuffer.allocate(8).putLong(value).array();
  }

  public String asString() {
    log.info("Getting string: " + new String(value));
    return new String(value);
  }

  public void setString(String value) {
    this.value = value.getBytes();
  }

  public byte[] asBytes() {
    return value;
  }

  public void setBytes(byte[] value) {
    this.value = value;
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
