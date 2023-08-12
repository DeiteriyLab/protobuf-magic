package protobuf.magic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.struct.ProtobufDecodingResult;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtobufMessageDecoder {
  static final Logger logging = new Logger(ProtobufMessageDecoder.class);

  public static ProtobufDecodingResult decodeProto(byte[] buffer)
      throws InsufficientResourcesException {

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
        ProtobufFieldValue field = new ProtobufFieldValue(ProtobufFieldType.fromValue(type), value);
        parts.add(new ProtobufField(byteRange, index, field));
      }
    } catch (UnknownTypeException err) {
      reader.resetToCheckpoint();
    }

    return new ProtobufDecodingResult(parts, reader.readBuffer(reader.leftBytes()), leftBytes);
  }

  private static String readValueBasedOnType(BufferReader reader, int type)
      throws UnknownTypeException, InsufficientResourcesException {
    ProtobufFieldType fieldType = ProtobufFieldType.fromValue(type);
    if (fieldType == null) {
      throw new UnknownTypeException("Unknown type: " + type);
    }

    switch (fieldType) {
      case VARINT:
        return reader.readVarInt().toString();
      case LENDELIM:
        BigInteger length = reader.readVarInt();
        return new String(reader.readBuffer(length.intValue()));
      case FIXED32:
        return new String(reader.readBuffer(4));
      case FIXED64:
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

  public static String typeToString(int type, String subType) {
    try {
      return ProtobufFieldType.fromValue(type).getName() + (subType != null ? ":" + subType : "");
    } catch (UnknownTypeException e) {
      logging.logToError("Unknown type: " + type);
      return "unknown";
    }
  }
}
