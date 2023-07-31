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
  public static ProtobufDecodingResult decodeProto(byte[] buffer)
      throws InsufficientResourcesException {

    BufferReader reader = new BufferReader(buffer);
    List<ProtobufField> parts = new ArrayList<>();

    reader.trySkipGrpcHeader();

    try {
      parts = processFields(reader);
    } catch (UnknownTypeException err) {
      reader.resetToCheckpoint();
    }

    return new ProtobufDecodingResult(parts, reader.readBuffer(reader.leftBytes()));
  }

  private static List<ProtobufField> processFields(BufferReader reader)
      throws UnknownTypeException, InsufficientResourcesException {

    List<ProtobufField> parts = new ArrayList<>();

    while (reader.leftBytes() > 0) {
      reader.checkpoint();

      int[] byteRange = {reader.getOffset()};
      int indexType = reader.readVarInt().intValue();
      int type = indexType & 0b111;
      int index = indexType >> 3;

      String value;
      if (type == ProtobufFieldType.VARINT.getValue()) {
        value = reader.readVarInt().toString();
      } else if (type == ProtobufFieldType.LENDELIM.getValue()) {
        BigInteger length = reader.readVarInt();
        value = new String(reader.readBuffer(length.intValue()));
      } else if (type == ProtobufFieldType.FIXED32.getValue()) {
        value = new String(reader.readBuffer(4));
      } else if (type == ProtobufFieldType.FIXED64.getValue()) {
        value = new String(reader.readBuffer(8));
      } else {
        throw new UnknownTypeException("Unknown type: " + type);
      }

      byteRange = appendToArray(byteRange, reader.getOffset());
      ProtobufFieldValue field = new ProtobufFieldValue(ProtobufFieldType.fromValue(type), value);
      parts.add(new ProtobufField(byteRange, index, field));
    }

    return parts;
  }

  private static int[] appendToArray(int[] array, int value) {
    int[] newArray = new int[array.length + 1];
    System.arraycopy(array, 0, newArray, 0, array.length);
    newArray[array.length] = value;
    return newArray;
  }

  public static String typeToString(int type, String subType) {
    if (type == ProtobufFieldType.VARINT.getValue()) {
      return "varint";
    }
    if (type == ProtobufFieldType.LENDELIM.getValue()) {
      return subType != null ? subType : "len_delim";
    }
    if (type == ProtobufFieldType.FIXED32.getValue()) {
      return "fixed32";
    }
    if (type == ProtobufFieldType.FIXED64.getValue()) {
      return "fixed64";
    }
    return "unknown";
  }
}
