package protobuf.magic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import protobuf.magic.Part;

public class ProtoDecoder {
  private static final int VARINT = 0;
  private static final int FIXED64 = 1;
  private static final int LENDELIM = 2;
  private static final int FIXED32 = 5;

  public static class DecodeResult {
    List<Part> parts;
    byte[] leftOver;

    DecodeResult(List<Part> parts, byte[] leftOver) {
      this.parts = parts;
      this.leftOver = leftOver;
    }
  }

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
        if (type == VARINT) {
          value = reader.readVarInt();
        } else if (type == LENDELIM) {
          BigInteger length = reader.readVarInt();
          value = reader.readBuffer(length.intValue());
        } else if (type == FIXED32) {
          value = reader.readBuffer(4);
        } else if (type == FIXED64) {
          value = reader.readBuffer(8);
        } else {
          throw new RuntimeException("Unknown type: " + type);
        }

        byteRange = appendToArray(byteRange, reader.getOffset());
        parts.add(new Part(Array.stream(byteRange), index, type, value));
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
    switch (type) {
      case VARINT:
        return "varint";
      case LENDELIM:
        return subType != null ? subType : "len_delim";
      case FIXED32:
        return "fixed32";
      case FIXED64:
        return "fixed64";
      default:
        return "unknown";
    }
  }
}
