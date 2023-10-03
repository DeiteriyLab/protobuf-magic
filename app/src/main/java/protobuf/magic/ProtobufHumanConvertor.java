package protobuf.magic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.protobuf.ProtobufMessageDecoder;
import protobuf.magic.struct.Protobuf;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtobufHumanConvertor {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static Protobuf decodeFromHuman(String humanJson)
      throws JsonMappingException, JsonProcessingException {
    JsonNode jsonNode = objectMapper.readTree(humanJson);
    return decodeJsonToProtobuf(jsonNode);
  }
  public static String encodeToHuman(Protobuf protobuf)
      throws JsonProcessingException {
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
        encodeProtobufToJson(protobuf));
  }

  private static Protobuf decodeJsonToProtobuf(JsonNode jsonNode) {
    List<ProtobufField> fields = new ArrayList<>();
    jsonNode.forEach(fieldNode -> {
      int index = fieldNode.get("index").asInt();
      ProtobufFieldType type = ProtobufFieldType.LEN;
      byte[] value = "INVALID".getBytes();
      try {
        type = ProtobufFieldType.fromName(fieldNode.get("type").asText());
        value = fieldNode.get("value").asText().getBytes("UTF-8");
      } catch (UnknownTypeException | IOException e) {
        e.printStackTrace();
      }
      fields.add(new ProtobufField(new int[0], index,
                                   new ProtobufFieldValue(type, value)));
    });
    return new Protobuf(fields, new byte[0], 0);
  }

  private static JsonNode encodeProtobufToJson(Protobuf protobuf) {
    List<JsonNode> fieldNodes = new ArrayList<>();
    for (ProtobufField field : protobuf.getProtobufFields()) {
      fieldNodes.add(encodeFieldToJson(field));
    }
    return objectMapper.valueToTree(fieldNodes);
  }

  private static JsonNode encodeFieldToJson(ProtobufField field) {
    String type = field.getType().name();
    Object value;

    if (field.getType() == ProtobufFieldType.LEN) {
      String val = new String(field.getValue());
      try {
        Protobuf attachment =
            ProtobufMessageDecoder.decodeProto(field.getValue());
        value = encodeProtobufToJson(attachment);
      } catch (Exception e) {
        value = val;
      }
    } else {
      try {
        value = new String(field.getValue(), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        value = field.getValue();
      }
    }

    return objectMapper.createObjectNode()
        .put("index", field.getIndex())
        .put("type", type)
        .putPOJO("value", value);
  }
}
