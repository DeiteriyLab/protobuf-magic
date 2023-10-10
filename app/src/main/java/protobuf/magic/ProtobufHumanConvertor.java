package protobuf.magic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.protobuf.ProtobufEncoder;
import protobuf.magic.protobuf.ProtobufMessageDecoder;
import protobuf.magic.protobuf.VarintUtils;
import protobuf.magic.struct.Protobuf;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtobufHumanConvertor {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static JsonNode prevJson;

  public static Protobuf decodeFromHuman(String humanJson)
      throws JsonMappingException, JsonProcessingException {
    JsonNode jsonNode = objectMapper.readTree(humanJson);
    if (prevJson == null) {
      prevJson = jsonNode;
    }
    jsonNode = JsonUpdater.updateProtobufFromJson(prevJson, jsonNode);
    prevJson = jsonNode;
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

        if (type == ProtobufFieldType.VARINT) {
          JsonNode valueNode = fieldNode.get("value");
          if (valueNode.has("int")) {
            BigInteger iint = new BigInteger(valueNode.get("int").asText());
            value = iint.toString().getBytes();
          }
        } else if (type == ProtobufFieldType.LEN) {
          var valNode = fieldNode.get("value");
          if (valNode.isTextual()) {
            value = valNode.asText().getBytes("UTF-8");
          } else if (valNode.isArray()) {
            Protobuf attachment = decodeJsonToProtobuf(valNode);
            value =
                ProtobufEncoder.encodeToProtobuf(attachment).getBytes("UTF-8");
          }
        } else {
          value = fieldNode.get("value").asText().getBytes("UTF-8");
        }
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
      value = new String(field.getValue());
      try {
        Protobuf attachment =
            ProtobufMessageDecoder.decodeProto(field.getValue());
        if (attachment.getLeftOver().length == 0) {
          value = encodeProtobufToJson(attachment);
        }
      } catch (Exception e) {
        System.err.println(e);
      }
    } else if (field.getType() == ProtobufFieldType.VARINT) {
      String val = new String(field.getValue());
      try {
        BigInteger iint = new BigInteger(val);
        BigInteger sint = VarintUtils.interpretAsSignedType(iint);
        value =
            objectMapper.createObjectNode().put("sint", iint).put("int", sint);
      } catch (NumberFormatException e) {
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
