package protobuf.magic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ProtoDecoder {
  public static DecodeResult decodeProto(byte[] buffer) {
    BufferReader reader = new BufferReader(buffer);
    List<Part> parts = new ArrayList<>();

    reader.trySkipGrpcHeader();

    try {
      while (reader.leftBytes() > 0) {
        reader.checkpoint();

        int[] byteRange = {reader.getOffset()};
        int indexType = reader.readVarInt().intValue();
        int type = indexType & 0b111;
        int index = indexType >> 3;

        Object value;
        if (type == TYPES.VARINT.getValue()) {
          value = reader.readVarInt();
        } else if (type == TYPES.LENDELIM.getValue()) {
          BigInteger length = reader.readVarInt();
          value = reader.readBuffer(length.intValue());
        } else if (type == TYPES.FIXED32.getValue()) {
          value = reader.readBuffer(4);
        } else if (type == TYPES.FIXED64.getValue()) {
          value = reader.readBuffer(8);
        } else {
          throw new RuntimeException("Unknown type: " + type);
        }

        byteRange = appendToArray(byteRange, reader.getOffset());
        parts.add(new Part(byteRange, index, type, value));
      }
    } catch (RuntimeException err) {
      reader.resetToCheckpoint();
    }

    return new DecodeResult(parts, reader.readBuffer(reader.leftBytes()));
  }

  private static int[] appendToArray(int[] array, int value) {
    int[] newArray = new int[array.length + 1];
    System.arraycopy(array, 0, newArray, 0, array.length);
    newArray[array.length] = value;
    return newArray;
  }

  public static String typeToString(int type, String subType) {
    if (type == TYPES.VARINT.getValue()) {
      return "varint";
    }
    if (type == TYPES.LENDELIM.getValue()) {
      return subType != null ? subType : "len_delim";
    }
    if (type == TYPES.FIXED32.getValue()) {
      return "fixed32";
    }
    if (type == TYPES.FIXED64.getValue()) {
      return "fixed64";
    }
    return "unknown";
  }
}
