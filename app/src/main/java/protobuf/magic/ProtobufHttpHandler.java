package protobuf.magic;

import static burp.api.montoya.http.handler.RequestToBeSentAction.continueWith;
import static burp.api.montoya.http.handler.ResponseReceivedAction.continueWith;
import static burp.api.montoya.http.message.params.HttpParameter.urlParameter;

import java.util.List;
import java.util.Optional;

import javax.naming.InsufficientResourcesException;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.logging.Logging;
import protobuf.magic.protobuf.ProtobufEncoder;
import protobuf.magic.protobuf.ProtobufMessageDecoder;
import protobuf.magic.struct.Protobuf;

class ProtobufHttpHandler implements HttpHandler {
	private final static Logger logging = new Logger(ProtobufHttpHandler.class);

  public ProtobufHttpHandler(MontoyaApi api) {
  }

  @Override
  public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
    if(!hasProtobuf(requestToBeSent.headers())) {
    	return continueWith(requestToBeSent);
    }
    String body = requestToBeSent.bodyToString();
    String output = fromHumanToProtobuf(body);
    HttpRequest modifiedRequest = requestToBeSent.withBody(output);
    return continueWith(modifiedRequest);
  }

  @Override
  public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
	  if(!hasProtobuf(responseReceived.headers())) {
		  return continueWith(responseReceived);
	  }
	  String humanProtobuf = fromHumanToProtobuf(responseReceived.bodyToString());
	  HttpResponse modified = responseReceived.withBody(humanProtobuf);
	  
    return continueWith(modified);
  }

  private static boolean hasProtobuf(List<HttpHeader> headers) {
    Optional<?> contentTypeHeader =
        headers.stream()
            .filter(
                h ->
                    h.name().equalsIgnoreCase("Content-Type")
                        && h.value().contains("application/grpc-web-text"))
            .findFirst();

    return contentTypeHeader.isPresent();
  }
  
  private String fromProtobufToHuman(String rawProtobuf) {
    byte[] bytes = EncodingUtils.parseInput(rawProtobuf);
    String output;
    try {
      var protobuf = ProtobufMessageDecoder.decodeProto(bytes);
      output = ProtobufJsonConverter.encodeToJson(protobuf).toString();
    } catch (InsufficientResourcesException e) {
      logging.logToError(e);
      output = "Insufficient resources";
    }
    return output;
  }

  private String fromHumanToProtobuf(String human) {
	  System.out.println(human);
          Protobuf res = ProtobufJsonConverter.decodeFromJson(human);
          String input = ProtobufEncoder.encodeToProtobuf(res);
          return input;
  }
}