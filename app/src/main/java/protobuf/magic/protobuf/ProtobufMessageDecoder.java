package protobuf.magic.protobuf;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.struct.Protobuf;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtobufMessageDecoder {
  public static Protobuf decodeProto(byte[] buffer) throws InsufficientResourcesException {

    BufferReader reader = new BufferReader(buffer);
    List<ProtobufField> parts = new ArrayList<>();

    reader.trySkipGrpcHeader();

    int leftBytes = 0;
    try {
      leftBytes = reader.leftBytes();
      while (reader.leftBytes() > 0) {
        reader.checkpoint();

        int[] byteRange = {reader.getOffset()};
        int indexType = reader.readVarInt().intValue();
        int type = indexType & 0b111;
        int index = indexType >> 3;

        String value = readValueBasedOnType(reader, type);

        byteRange = appendToArray(byteRange, reader.getOffset());
        ProtobufFieldValue field =
            new ProtobufFieldValue(
                ProtobufFieldType.fromValue(type), value.getBytes(Charset.forName("UTF-8")));
        parts.add(new ProtobufField(byteRange, index, field));
      }
    } catch (UnknownTypeException err) {
      reader.resetToCheckpoint();
    }

    return new Protobuf(parts, reader.readBuffer(reader.leftBytes()), leftBytes);
  }

  private static String readValueBasedOnType(BufferReader reader, int type)
      throws UnknownTypeException, InsufficientResourcesException {
    ProtobufFieldType fieldType = ProtobufFieldType.fromValue(type);

    switch (fieldType) {
      case VARINT:
        return reader.readVarInt().toString();
      case LEN:
        BigInteger length = reader.readVarInt();
        return new String(reader.readBuffer(length.intValue()));
      case I32:
        return new String(reader.readBuffer(4));
      case I64:
        return new String(reader.readBuffer(8));
      default:
        throw new UnknownTypeException("Unknown type: " + fieldType.getName());
    }
  }

  private static int[] appendToArray(int[] array, int value) {
    int[] newArray = new int[array.length + 1];
    System.arraycopy(array, 0, newArray, 0, array.length);
    newArray[array.length] = value;
    return newArray;
  }
}
