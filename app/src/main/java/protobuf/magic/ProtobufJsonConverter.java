package protobuf.magic;

import java.util.ArrayList;
import java.util.List;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.struct.*;

public class ProtobufJsonConverter {
  public static ProtobufDecodingResult decodeFromJson(String jsonString) {
    List<ProtobufField> protobufFields = new ArrayList<>();

    jsonString = jsonString.replaceAll("\n", "").replaceAll(" ", "");
    String[] pairs = jsonString.substring(1, jsonString.length() - 1).split(",");
    int index = 0;
    for (String pair : pairs) {
      String[] keyValue = pair.split(":");
      String key = keyValue[0].trim().substring(1, keyValue[0].length() - 1);
      String value = keyValue[1].trim().substring(1, keyValue[1].length() - 1);

      ProtobufFieldType type;
      try {
        type = ProtobufFieldType.fromName(key);
      } catch (UnknownTypeException e) {
        System.err.println("Unknown type: " + key); // TODO: use logger burp suite
        continue;
      }
      ProtobufFieldValue protobufFieldValue = new ProtobufFieldValue(type, value);

      int[] byteRange = new int[0];

      ProtobufField protobufField = new ProtobufField(byteRange, index++, protobufFieldValue);
      protobufFields.add(protobufField);
    }

    byte[] leftOver = new byte[0];

    return new ProtobufDecodingResult(protobufFields, leftOver);
  }

  public static String encodeToJson(ProtobufDecodingResult result) {
    StringBuilder jsonObject = new StringBuilder("{");

    for (ProtobufField field : result.getProtobufFields()) {
      jsonObject.append("\"").append(field.getProtobuf().getType().getName()).append("\": ");
      jsonObject.append("\"").append(field.getProtobuf().getValue()).append("\", ");
    }

    if (jsonObject.length() > 1) {
      jsonObject.setLength(jsonObject.length() - 2);
    }

    jsonObject.append("}");

    return jsonObject.toString();
  }
}
