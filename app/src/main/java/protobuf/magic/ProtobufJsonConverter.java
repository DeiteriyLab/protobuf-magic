package protobuf.magic;

import java.util.ArrayList;
import java.util.List;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.struct.ProtobufDecodingResult;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtobufJsonConverter {
  static final Logger logging = new Logger(ProtobufJsonConverter.class);

  public static ProtobufDecodingResult decodeFromJson(String jsonString) {
    List<ProtobufField> protobufFields = new ArrayList<>();

    jsonString = jsonString.replaceAll("\n", "").replaceAll(" ", "");
    String[] pairs = jsonString.substring(1, jsonString.length() - 1).split(",");
    int index = 0;
    byte[] leftOver = new byte[0];
    int lenLeftOver = 0;
    for (String pair : pairs) {
      String[] keyValue = pair.split(":");
      String key = keyValue[0].trim().substring(1, keyValue[0].length() - 1);
      String value = keyValue[1].trim().substring(1, keyValue[1].length() - 1);

      if (key == "leftOver") {
        lenLeftOver = Integer.parseInt(value);
        continue;
      }

      ProtobufFieldType type;
      try {
        type = ProtobufFieldType.fromName(key);
      } catch (UnknownTypeException e) {
        logging.logToError("Unknown type: " + key);
        continue;
      }
      ProtobufFieldValue protobufFieldValue = new ProtobufFieldValue(type, value);

      int[] byteRange = new int[0];

      ProtobufField protobufField = new ProtobufField(byteRange, index++, protobufFieldValue);
      protobufFields.add(protobufField);
    }

    return new ProtobufDecodingResult(protobufFields, leftOver, lenLeftOver);
  }

  public static String encodeToJson(ProtobufDecodingResult result) {
    StringBuilder jsonObject = new StringBuilder("{");

    for (ProtobufField field : result.getProtobufFields()) {
      jsonObject.append("\"").append(field.getProtobuf().getType().getName()).append("\": ");
      jsonObject.append("\"").append(field.getProtobuf().getValue()).append("\", ");
    }

    jsonObject.append("\"leftOver\": ").append("\"").append(result.getLenLeftOver()).append("\"");

    jsonObject.append("}");

    return jsonObject.toString();
  }
}
