package protobuf.magic.adapter.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.CustomLog;
import protobuf.magic.adapter.importer.BinaryToProtobuf;
import protobuf.magic.exception.*;
import protobuf.magic.struct.ByteRange;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Node;
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

  private static Field decodeFieldFromJson(JsonNode node)
      throws UnknownTypeException, UnknownStructException {
    int index = node.get("index").asInt();
    String stype = node.get("type").asText();
    int start = node.get("start").asInt();
    int end = node.get("end").asInt();
    Type type = Type.fromName(stype);
    byte[] value = decodeValueFromJson(node, type);
    return new Field(index, type, new Node(value), new ByteRange(start, end));
  }

  private static byte[] decodeValueFromJson(JsonNode fieldNode, Type type)
      throws UnknownStructException {
    JsonNode valNode = fieldNode.get("value");
    byte[] bytes = valNode.asText("").getBytes();
    try {
      return (type == Type.LEN) ? decodeLenDelim(valNode) : bytes;
    } catch (UnknownTypeException e) {
      log.error(e);
    }
    return bytes;
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

  private static JsonNode protobufToJson(DynamicProtobuf protobuf) {
    List<JsonNode> nodes = new ArrayList<>();
    for (Field field : protobuf.fields()) {
      nodes.add(encodeFieldToJson(field));
    }
    return OBJECT_MAPPER.valueToTree(nodes);
  }

  private static JsonNode encodeFieldToJson(Field field) {
    Object value = field.parseValue();
    if (field.type() == Type.LEN) {
      value = handleLenType(field.value());
    }
    return OBJECT_MAPPER
        .createObjectNode()
        .put("index", field.index())
        .put("type", field.type().getName())
        .put("start", field.byterange().start())
        .put("end", field.byterange().end())
        .putPOJO("value", value);
  }

  private static Object handleLenType(Node value) {
    try {
      DynamicProtobuf proto = value.asProtobuf();
      if (proto.leftOver().length == 0) {
        var fields = proto.fields();
        List<JsonNode> fieldNodes = new ArrayList<>();
        for (var field : fields) {
          fieldNodes.add(encodeFieldToJson(field));
        }

        return fieldNodes;
      }
    } catch (Exception e) {
      log.error(e);
    }
    return value.asString();
  }
}
