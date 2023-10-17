package protobuf.magic.adapter.importer;

import java.util.ArrayList;
import java.util.List;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.adapter.*;
import protobuf.magic.exception.UnknownStructException;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.protobuf.BufferReader;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
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
    List<Field> parts = new ArrayList<>();

    reader.trySkipGrpcHeader();
    processBuffer(reader, parts);

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

  private static void processBuffer(BufferReader reader, List<Field> parts)
      throws UnknownTypeException, InsufficientResourcesException {
    while (reader.leftBytes() > 0) {
      reader.checkpoint();
      int indexType = reader.readVarInt().intValue();
      int type = indexType & 0b111;
      int index = indexType >> 3;

      String value = readValueBasedOnType(reader, type);
      parts.add(new Field(index, Type.fromValue(type), value));
    }
  }

  private static String readValueBasedOnType(BufferReader reader, int type)
      throws UnknownTypeException, InsufficientResourcesException {
    Type fieldType = Type.fromValue(type);
    return switch (fieldType) {
      case VARINT -> reader.readVarInt().toString();
      case LEN -> new String(reader.readBuffer(reader.readVarInt().intValue()));
      case I32 -> new String(reader.readBuffer(4));
      case I64 -> new String(reader.readBuffer(8));
      default -> throw new UnknownTypeException("Unknown type: " + fieldType.getName());
    };
  }
}