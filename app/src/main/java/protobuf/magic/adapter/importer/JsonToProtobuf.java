package protobuf.magic.adapter.importer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.CustomLog;
import protobuf.magic.adapter.exporter.ProtobufToBinary;
import protobuf.magic.exception.UnknownStructException;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Type;

@CustomLog
public class JsonToProtobuf implements HumanReadableToProtobuf {
  private static final String INVALID = "INVALID";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final ProtobufToBinary PROTOBUF_TO_BINARY = new ProtobufToBinary();

  @Override
  public DynamicProtobuf convert(String str) throws UnknownStructException {
    return stringToProtobuf(str);
  }

  private static DynamicProtobuf stringToProtobuf(String str) throws UnknownStructException {
    JsonNode jsonNode = stringToJson(str);
    try {
      return jsonToProtobuf(jsonNode);
    } catch (UnknownTypeException e) {
      log.error(e);
      throw new UnknownStructException("Type not found");
    } catch (UnknownStructException e) {
      log.error(e);
      throw new UnknownStructException("Unknown struct");
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

  private static DynamicProtobuf jsonToProtobuf(JsonNode jsonNode)
      throws UnknownTypeException, UnknownStructException {
    if (jsonNode == null) {
      throw new UnknownStructException("Invalid JSON");
    }
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
    if (fieldNode == null) return false;
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
}
