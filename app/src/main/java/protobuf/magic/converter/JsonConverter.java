package protobuf.magic.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.CustomLog;
import protobuf.magic.Config;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Type;

@CustomLog
public class JsonConverter extends FormatConverter {
  private static final String INVALID = "INVALID";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public JsonConverter() {
    super(JsonConverter::stringToProtobuf, JsonConverter::protobufToString);
  }

  private static DynamicProtobuf stringToProtobuf(String str) {
    JsonNode jsonNode = stringToJson(str);
    try {
      return jsonToProtobuf(jsonNode);
    } catch (UnknownTypeException e) {
      log.error(e);
      Field field = new Field(0, Type.LEN, INVALID);
      return new DynamicProtobuf(List.of(field), new byte[0]);
    }
  }

  private static JsonNode stringToJson(String str) {
    try {
      return OBJECT_MAPPER.readTree(str);
    } catch (JsonProcessingException e) {
      log.error(e);
      return null;
    }
  }

  private static String protobufToString(DynamicProtobuf protobuf) {
    JsonNode jsonNode = protobufToJson(protobuf);
    try {
      return jsonToString(jsonNode);
    } catch (JsonProcessingException e) {
      log.error(e);
      return INVALID;
    }
  }

  private static String jsonToString(JsonNode jsonNode)
      throws JsonProcessingException {
    return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(
        jsonNode);
  }

  private static DynamicProtobuf jsonToProtobuf(JsonNode jsonNode)
      throws UnknownTypeException {
    List<Field> fields = new ArrayList<>();
    for (int i = 0; i < jsonNode.size(); i++) {
      JsonNode fieldNode = jsonNode.get(i);
      int index = fieldNode.get("index").asInt();
      Type type = Type.fromName(fieldNode.get("type").asText());
      String value = decodeValueFromJson(fieldNode, type);
      fields.add(new Field(index, type, value));
    }
    return new DynamicProtobuf(fields, new byte[0]);
  }

  private static String decodeValueFromJson(JsonNode fieldNode, Type type) {
    JsonNode valNode = fieldNode.get("value");
    try {
      return (type == Type.LEN) ? new String(decodeLenDelim(valNode))
                                : valNode.asText();
    } catch (UnknownTypeException e) {
      log.error(e);
      return valNode.asText();
    }
  }

  private static byte[] decodeLenDelim(JsonNode valNode)
      throws UnknownTypeException {
    if (valNode.isTextual()) {
      return valNode.asText().getBytes();
    } else if (valNode.isArray()) {
      DynamicProtobuf attachment = jsonToProtobuf(valNode);
      List<Byte> arr =
          Config.convertBinaryProtobuf().convertFromEntity(attachment);
      byte[] arrr = new byte[arr.size()];
      for (int i = 0; i < arr.size(); i++) {
        arrr[i] = arr.get(i);
      }
      return arrr;
    }
    log.debug(String.format("Decoding error: %s", valNode));
    return INVALID.getBytes();
  }

  private static JsonNode protobufToJson(DynamicProtobuf protobuf) {
    List<JsonNode> fieldNodes = new ArrayList<>();
    for (Field field : protobuf.fields()) {
      fieldNodes.add(encodeFieldToJson(field));
    }
    return OBJECT_MAPPER.valueToTree(fieldNodes);
  }

  private static JsonNode encodeFieldToJson(Field field) {
    String type = field.type().name();
    Object value = fieldValue(field);
    return OBJECT_MAPPER.createObjectNode()
        .put("index", field.index())
        .put("type", type)
        .putPOJO("value", value);
  }

  private static Object fieldValue(Field field) {
    Object value = field.value();
    if (field.type() == Type.LEN) {
      return handleLenType(field, value);
    } else if (field.type() == Type.VARINT) {
      return new BigInteger((String)value);
    } else {
      return value;
    }
  }

  private static Object handleLenType(Field field, Object value) {
    log.debug(String.format("handleLenType %s %s", field, value));
    String str = (String)value;
    List<Byte> bstr = new ArrayList<>();
    for (int i = 0; i < str.length(); i++) {
      bstr.add((byte)str.charAt(i));
    }
    DynamicProtobuf attachment =
        Config.convertBinaryProtobuf().convertFromDTO(bstr);
    System.out.println("!!!!!!" + attachment.leftOver().length);
    if (attachment.leftOver().length == 0) {
      return protobufToJson(attachment);
    }
    return value;
  }
}
