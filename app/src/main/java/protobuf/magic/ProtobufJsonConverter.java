package protobuf.magic;

import java.util.ArrayList;
import java.util.List;
import protobuf.magic.struct.*;

public class ProtobufJsonConverter {
  public static ProtobufDecodingResult decodeFromJson(String jsonString) {
    List<ProtobufField> protobufFields = new ArrayList<>();

    String[] pairs = jsonString.substring(1, jsonString.length() - 2).split(",");
    for (String pair : pairs) {
      String[] keyValue = pair.split(":");
      String key = keyValue[0].trim().substring(1, keyValue[0].length() - 2);
      String value = keyValue[1].trim().substring(1, keyValue[1].length() - 2);

      ProtobufFieldType type = ProtobufFieldType.valueOf(key.toUpperCase());
      ProtobufFieldValue protobufFieldValue = new ProtobufFieldValue(type, value);

      int[] byteRange = new int[0];
      int index = 0;

      ProtobufField protobufField = new ProtobufField(byteRange, index, protobufFieldValue);
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
