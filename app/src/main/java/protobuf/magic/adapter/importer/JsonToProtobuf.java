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
import protobuf.magic.struct.ByteRange;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Node;
import protobuf.magic.struct.Type;

@CustomLog
public class JsonToProtobuf implements HumanReadableToProtobuf {
  private static final String INVALID = "INVALID";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final ProtobufToBinary PROTOBUF_TO_BINARY = new ProtobufToBinary();

  @Override
  public DynamicProtobuf convert(String str) throws UnknownStructException {
    log.info("JsonToProtobuf: " + str);
    JsonNode jsonNode = stringToJson(str);
    try {
      DynamicProtobuf proto = jsonToProtobuf(jsonNode);
      log.info("JsonToProtobuf result: " + proto);
      return proto;
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
    boolean has =
        fieldNode.has("index")
            && fieldNode.has("type")
            && fieldNode.has("value")
            && fieldNode.has("start")
            && fieldNode.has("end");
    if (!has) return has;
    boolean indexIsValid = false;
    boolean startIsValid = false;
    boolean endIsValid = false;
    try {
      int index = Integer.parseInt(fieldNode.get("index").asText());
      indexIsValid = index >= 0;
      int start = Integer.parseInt(fieldNode.get("start").asText());
      startIsValid = start >= 0;
      int end = Integer.parseInt(fieldNode.get("end").asText());
      endIsValid = end >= 0;
    } catch (NumberFormatException e) {
      indexIsValid = false;
      log.error(e);
    }
    boolean type = indexIsValid && startIsValid && endIsValid;
    return has && type;
  }

  private static Field decodeFieldFromJson(JsonNode node)
      throws UnknownTypeException, UnknownStructException {
    int index = node.get("index").asInt();
    String stype = node.get("type").asText();
    int start = node.get("start").asInt(0);
    int end = node.get("end").asInt(0);
    Type type = Type.fromName(stype);
    Node value = decodeValueFromJson(node, type);
    return new Field(index, type, value, new ByteRange(start, end));
  }

  // @FIXME dublicate method JsonToProtobuf and ProtobufToJson
  private static Node decodeValueFromJson(JsonNode field, Type type) throws UnknownStructException {
    JsonNode node = field.get("value");
    byte[] bytes = node.asText("").getBytes();
    Node value = new Node(bytes);
    try {
      if (type == Type.LEN) {
        value.setBytes(decodeLenDelim(node));
      } else if (type == Type.VARINT) {
        value.setLong(node.asLong());
      } else if (type == Type.I32) {
        value.setFloat((float) node.asDouble());
      } else if (type == Type.I64) {
        value.setDouble(node.asDouble());
      }
    } catch (UnknownTypeException e) {
      log.error(e);
    }
    return value;
  }

  private static byte[] decodeLenDelim(JsonNode valNode)
      throws UnknownTypeException, UnknownStructException {
    if (valNode.isTextual()) {
      return valNode.asText().getBytes();
    } else if (valNode.isArray() || valNode.isObject()) {
      DynamicProtobuf attachment = jsonToProtobuf(valNode);
      List<Byte> list = PROTOBUF_TO_BINARY.convert(attachment);
      byte[] array = new byte[list.size()];
      for (int i = 0; i < list.size(); i++) {
        array[i] = list.get(i);
      }
      return array;
    }
    log.error(String.format("Decoding error: %s", valNode));
    return INVALID.getBytes();
  }
}
