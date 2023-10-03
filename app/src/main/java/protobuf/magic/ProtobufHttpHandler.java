package protobuf.magic;

import static burp.api.montoya.http.handler.RequestToBeSentAction.continueWith;
import static burp.api.montoya.http.handler.ResponseReceivedAction.continueWith;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Optional;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.protobuf.ProtobufEncoder;
import protobuf.magic.protobuf.ProtobufMessageDecoder;
import protobuf.magic.struct.Protobuf;

class ProtobufHttpHandler implements HttpHandler {
  private static final Logger logging = new Logger(ProtobufHttpHandler.class);

  public ProtobufHttpHandler(MontoyaApi api) {}

  @Override
  public RequestToBeSentAction
  handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
    if (!hasProtobuf(requestToBeSent.headers())) {
      return continueWith(requestToBeSent);
    }
    String body = requestToBeSent.bodyToString();
    String output = fromHumanToProtobuf(body);

    HttpRequest modifiedRequest = requestToBeSent.withBody(output);
    return continueWith(modifiedRequest);
  }

  @Override
  public ResponseReceivedAction
  handleHttpResponseReceived(HttpResponseReceived responseReceived) {
    if (!hasProtobuf(responseReceived.headers())) {
      return continueWith(responseReceived);
    }

    return continueWith(responseReceived);
  }

  private static boolean hasProtobuf(List<HttpHeader> headers) {
    Optional<?> contentTypeHeader =
        headers.stream()
            .filter(h
                    -> h.name().equalsIgnoreCase("Content-Type") &&
                           h.value().startsWith("application/grpc-web-text"))
            .findFirst();

    return contentTypeHeader.isPresent();
  }

  private String fromProtobufToHuman(String rawProtobuf) {
    byte[] bytes = EncodingUtils.parseInput(rawProtobuf);
    String output;
    try {
      var protobuf = ProtobufMessageDecoder.decodeProto(bytes);
      output = ProtobufHumanConvertor.encodeToHuman(protobuf).toString();
    } catch (InsufficientResourcesException | JsonProcessingException e) {
      logging.logToError(e);
      output = "Insufficient resources";
    }
    return output;
  }

  private String fromHumanToProtobuf(String human) {
    if (!checkHumanFormat(human)) {
      return human;
    }
    Protobuf res = null;
    try {
      res = ProtobufHumanConvertor.decodeFromHuman(human);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return ProtobufEncoder.encodeToProtobuf(res);
  }

  private boolean checkHumanFormat(String str) {
    return str.contains("None:leftOver");
  }
}
