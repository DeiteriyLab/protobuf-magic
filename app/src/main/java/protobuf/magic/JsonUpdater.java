package protobuf.magic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigInteger;
import protobuf.magic.protobuf.VarintUtils;

public class JsonUpdater {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static JsonNode updateProtobufFromJson(JsonNode oldJson, JsonNode newJson) {

    for (int i = 0; i < oldJson.size(); i++) {
      JsonNode oldFieldNode = oldJson.get(i);
      JsonNode newFieldNode = newJson.get(i);

      if (oldFieldNode.get("type").asText().equals("VARINT")
          && newFieldNode.get("type").asText().equals("VARINT")) {
        JsonNode oldValueNode = oldFieldNode.get("value");
        JsonNode newValueNode = newFieldNode.get("value");

        BigInteger oldInt = new BigInteger(oldValueNode.get("int").asText());
        BigInteger oldSint = new BigInteger(oldValueNode.get("sint").asText());

        BigInteger newInt = new BigInteger(newValueNode.get("int").asText());
        BigInteger newSint = new BigInteger(newValueNode.get("sint").asText());

        BigInteger oldSintNormalized = VarintUtils.interpretAsSignedType(oldSint);
        BigInteger newSintNormalized = VarintUtils.interpretAsSignedType(newSint);

        if (!oldSintNormalized.equals(newSintNormalized) && oldInt.equals(newInt)) {

          ((ObjectNode) newValueNode).put("int", newSintNormalized.toString());
        } else if (oldSintNormalized.equals(newSintNormalized) && !oldInt.equals(newInt)) {

          ((ObjectNode) newValueNode).put("sint", newInt.toString());
        }
      }
    }
    return newJson;
  }
}
