package protobuf.magic.adapter.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.CustomLog;
import protobuf.magic.adapter.importer.BinaryToProtobuf;
import protobuf.magic.exception.*;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Type;

@CustomLog
public class ProtobufToJson implements ProtobufToHumanReadable {
  private static final String INVALID = "INVALID";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final ProtobufToBinary PROTOBUF_TO_BINARY = new ProtobufToBinary();
  private static final BinaryToProtobuf BINARY_TO_PROTOBUF = new BinaryToProtobuf();

  @Override
  public String convert(DynamicProtobuf protobuf) {
    return protobufToString(protobuf);
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

  private static String jsonToString(JsonNode jsonNode) throws JsonProcessingException {
    return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
  }

  private static DynamicProtobuf jsonToProtobuf(JsonNode jsonNode)
      throws UnknownTypeException, UnknownStructException {
    List<Field> fields = new ArrayList<>();
    for (var field : jsonNode) {
      if (checkValidField(field)) {
        fields.add(decodeFieldFromJson(field));
      } else {
        throw new UnknownStructException("Unknown struct: " + jsonNode);
      }
    }
    return new DynamicProtobuf(fields, new byte[0]);
  }

  private static boolean checkValidField(JsonNode fieldNode) {
    boolean has = fieldNode.has("index") && fieldNode.has("type") && fieldNode.has("value");
    if (!has) return has;
    boolean indexIsValid = true;
    try {
      int index = Integer.parseInt(fieldNode.get("index").asText());
      indexIsValid = index >= 0;
    } catch (NumberFormatException e) {
      indexIsValid = false;
      log.error(e);
    }
    boolean type = indexIsValid;
    return has && type;
  }

  private static Field decodeFieldFromJson(JsonNode fieldNode)
      throws UnknownTypeException, UnknownStructException {
    int index = fieldNode.get("index").asInt();
    String stype = fieldNode.get("type").asText();
    Type type = Type.fromName(stype);
    String value = decodeValueFromJson(fieldNode, type);
    return new Field(index, type, value);
  }

  private static String decodeValueFromJson(JsonNode fieldNode, Type type)
      throws UnknownStructException {
    JsonNode valNode = fieldNode.get("value");
    try {
      return (type == Type.LEN) ? new String(decodeLenDelim(valNode)) : valNode.asText();
    } catch (UnknownTypeException e) {
      log.error(e);
      return valNode.asText();
    }
  }

  private static byte[] decodeLenDelim(JsonNode valNode)
      throws UnknownTypeException, UnknownStructException {
    if (valNode.isTextual()) {
      return valNode.asText().getBytes();
    } else if (valNode.isArray() || valNode.isObject()) {
      DynamicProtobuf attachment = jsonToProtobuf(valNode);
      List<Byte> arr = PROTOBUF_TO_BINARY.convert(attachment);
      byte[] arrr = new byte[arr.size()];
      for (int i = 0; i < arr.size(); i++) {
        arrr[i] = arr.get(i);
      }
      return arrr;
    }
    log.error(String.format("Decoding error: %s", valNode));
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
    return OBJECT_MAPPER
        .createObjectNode()
        .put("index", field.index())
        .put("type", type)
        .putPOJO("value", value);
  }

  private static Object fieldValue(Field field) {
    Object value = field.value();
    if (field.type() == Type.LEN) {
      return handleLenType(field, value);
    } else if (field.type() == Type.VARINT) {
      return new BigInteger((String) value);
    } else {
      return value;
    }
  }

  private static Object handleLenType(Field field, Object value) {
    log.debug(String.format("handleLenType %s %s", field, value));
    String str = (String) value;
    List<Byte> bstr = new ArrayList<>();
    for (int i = 0; i < str.length(); i++) {
      bstr.add((byte) str.charAt(i));
    }
    try {
      DynamicProtobuf attachment = BINARY_TO_PROTOBUF.convert(bstr);
      if (bstr.size() > 0 && attachment.leftOver().length == 0) {
        return attachment.fields();
      }
    } catch (UnknownStructException e) {
      log.error(e);
    }
    return value;
  }
}
