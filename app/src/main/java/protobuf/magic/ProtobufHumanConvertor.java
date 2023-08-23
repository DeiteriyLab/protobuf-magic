package protobuf.magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.struct.Protobuf;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtobufHumanConvertor {
  static final Logger logging = new Logger(ProtobufHumanConvertor.class);

  public static Protobuf decodeFromHuman(String jsonString) {
    List<ProtobufField> protobufFields = new ArrayList<>();

    byte[] leftOver = new byte[0];
    int lenLeftOver = 0;
    String[] lines = jsonString.split("\n");
    for (String line : lines) {
      String[] parts = line.split(":");
      if (parts.length < 3) {
        System.err.println("Index out of bounds: " + line);
        continue;
      }
      int index = Integer.parseInt(parts[0]);
      String stype = parts[1];
      String value = Arrays.stream(parts, 3, parts.length)
                         .collect(Collectors.joining(" "));

      if (stype == "leftOver") {
        lenLeftOver = Integer.parseInt(value);
        continue;
      }

      ProtobufFieldType type;
      try {
        type = ProtobufFieldType.fromName(stype);
      } catch (UnknownTypeException e) {
        logging.logToError("Unknown type: " + stype);
        continue;
      }
      ProtobufFieldValue protobufFieldValue =
          new ProtobufFieldValue(type, value);

      int[] byteRange = new int[0];

      ProtobufField protobufField =
          new ProtobufField(byteRange, index, protobufFieldValue);
      protobufFields.add(protobufField);
    }

    return new Protobuf(protobufFields, leftOver, lenLeftOver);
  }

  public static String encodeToHuman(Protobuf result) {
    StringBuilder human = new StringBuilder();
    for (ProtobufField field : result.getProtobufFields()) {
      human.append(field.getIndex())
          .append(":")
          .append(field.getType())
          .append(":")
          .append(field.getValue())
          .append("\n");
    }
    human.append("0:leftOver:").append(result.getLenLeftOver());
    return human.toString();
  }
}
