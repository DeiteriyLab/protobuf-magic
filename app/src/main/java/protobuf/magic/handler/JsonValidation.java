package protobuf.magic.handler;

import com.fasterxml.jackson.databind.JsonNode;
import protobuf.magic.exception.ProcessHandlerException;

public class JsonValidation extends JsonHandler {
  @Override
  protected JsonNode makeHandler(JsonNode value) throws ProcessHandlerException {
    if (value == null) {
      throw new ProcessHandlerException("JSON is null");
    }

    boolean has = value.has("index") && value.has("type") && value.has("value");
    if (!has) {
      throw new ProcessHandlerException("Not has a key index or type or value in JSON");
    }
    boolean indexIsValid = true;
    try {
      int index = Integer.parseInt(value.get("index").asText());
      indexIsValid = index >= 0;
    } catch (NumberFormatException e) {
      indexIsValid = false;
    }
    boolean type = indexIsValid;
    if (has && type) {
      return next.makeHandler(value);
    } else {
      throw new ProcessHandlerException("Index or type is invalid");
    }
  }
}
