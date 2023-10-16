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
import protobuf.magic.converter.Converter;
import protobuf.magic.converter.HumanReadableToBinary;

@CustomLog
class ProtobufHttpHandler implements HttpHandler {
  private static final Converter<String, String> converter = new HumanReadableToBinary();

  public ProtobufHttpHandler(MontoyaApi api) {}

  @Override
  public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
    if (!hasProtobuf(requestToBeSent.headers())) {
      return continueWith(requestToBeSent);
    }
    String body = requestToBeSent.bodyToString();
    String output = converter.convertFromDTO(body);

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
