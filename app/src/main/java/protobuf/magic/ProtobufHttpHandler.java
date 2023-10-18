package protobuf.magic;

import static burp.api.montoya.http.handler.RequestToBeSentAction.continueWith;
import static burp.api.montoya.http.handler.ResponseReceivedAction.continueWith;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.HttpHandler;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.handler.RequestToBeSentAction;
import burp.api.montoya.http.handler.ResponseReceivedAction;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import java.util.List;
import java.util.Optional;
import lombok.CustomLog;
import protobuf.magic.adapter.BinaryToHumanReadable;
import protobuf.magic.adapter.HumanReadableToBinary;
import protobuf.magic.exception.UnknownStructException;

@CustomLog
class ProtobufHttpHandler implements HttpHandler {
  private static final HumanReadableToBinary humanToBinary = new HumanReadableToBinary();
  private static final BinaryToHumanReadable binaryToHuman = new BinaryToHumanReadable();

  public ProtobufHttpHandler(MontoyaApi api) {}

  @Override
  public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
    if (!hasProtobuf(requestToBeSent.headers())) {
      return continueWith(requestToBeSent);
    }
    String body = requestToBeSent.bodyToString();
    log.info("Http handler has changed: " + body);
    String output = body;
    try {
      output = humanToBinary.convert(body);
    } catch (UnknownStructException e) {
      log.error(e);
    }

    HttpRequest modifiedRequest = requestToBeSent.withBody(output);
    return continueWith(modifiedRequest);
  }

  @Override
  public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
    if (!hasProtobuf(responseReceived.headers())) {
      return continueWith(responseReceived);
    }

    return continueWith(responseReceived);
  }

  private static boolean hasProtobuf(List<HttpHeader> headers) {
    Optional<?> contentTypeHeader =
        headers.stream()
            .filter(
                h ->
                    h.name().equalsIgnoreCase("Content-Type")
                        && h.value().startsWith("application/grpc-web-text"))
            .findFirst();

    return contentTypeHeader.isPresent();
  }
}
