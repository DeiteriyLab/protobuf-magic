package protobuf.magic.adapter.importer;

import java.util.ArrayList;
import java.util.List;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.adapter.*;
import protobuf.magic.exception.UnknownStructException;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.protobuf.BufferReader;
import protobuf.magic.struct.ByteRange;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Node;
import protobuf.magic.struct.Type;

public class BinaryToProtobuf implements Converter<DynamicProtobuf, List<Byte>> {
  @Override
  public DynamicProtobuf convert(List<Byte> bytes) throws UnknownStructException {
    try {
      return bytesToProtobuf(bytes);
    } catch (InsufficientResourcesException | UnknownTypeException e) {
      throw new UnknownStructException(e);
    }
  }

  private static DynamicProtobuf bytesToProtobuf(List<Byte> bufferList)
      throws InsufficientResourcesException, UnknownTypeException {
    byte[] buffer = toArray(bufferList);
    BufferReader reader = new BufferReader(buffer);

    reader.trySkipGrpcHeader();
    List<Field> parts = processBuffer(reader);

    byte[] leftover = reader.readBuffer(reader.leftBytes());
    return new DynamicProtobuf(parts, leftover);
  }

  private static byte[] toArray(List<Byte> bufferList) {
    byte[] buffer = new byte[bufferList.size()];
    for (int i = 0; i < bufferList.size(); i++) {
      buffer[i] = bufferList.get(i);
    }
    return buffer;
  }

  private static List<Field> processBuffer(BufferReader reader)
      throws UnknownTypeException, InsufficientResourcesException {
    List<Field> parts = new ArrayList<>();
    try {
      while (reader.leftBytes() > 0) {
        reader.checkpoint();
        int start = reader.getOffset();
        int indexType = reader.readVarInt().intValue();
        int type = indexType & 0b111;
        int index = indexType >> 3;

        byte[] value = readValueBasedOnType(reader, type);
        int end = reader.getOffset();
        parts.add(
            new Field(index, Type.fromValue(type), new Node(value), new ByteRange(start, end)));
      }
    } catch (Exception e) {
      reader.resetToCheckpoint();
    }
    return parts;
  }

  private static byte[] readValueBasedOnType(BufferReader reader, int type)
      throws UnknownTypeException, InsufficientResourcesException {
    Type fieldType = Type.fromValue(type);
    return switch (fieldType) {
      case VARINT -> reader.readVarInt().toByteArray();
      case LEN -> reader.readBuffer(reader.readVarInt().intValue());
      case I32 -> reader.readBuffer(4);
      case I64 -> reader.readBuffer(8);
      default -> throw new UnknownTypeException("Unknown type: " + fieldType.getName());
    };
  }
}
