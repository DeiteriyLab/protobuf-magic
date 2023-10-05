package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.awt.Component;
import java.util.Optional;
import java.util.stream.*;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.protobuf.ProtobufEncoder;
import protobuf.magic.protobuf.ProtobufMessageDecoder;
import protobuf.magic.struct.Protobuf;

class ProtobufExtensionProvidedHttpRequestEditor implements ExtensionProvidedHttpRequestEditor {
  private final Logger logging = new Logger(ProtobufExtensionProvidedHttpRequestEditor.class);
  private final RawEditor requestEditor;
  private HttpRequestResponse requestResponse;
  private ParsedHttpParameter parsedHttpParameter;

  ProtobufExtensionProvidedHttpRequestEditor(
      MontoyaApi api, EditorCreationContext creationContext) {
    if (creationContext.editorMode() == EditorMode.READ_ONLY) {
      requestEditor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
    } else {
      requestEditor = api.userInterface().createRawEditor();
    }
  }

  @Override
  public HttpRequest getRequest() {
    HttpRequest request;

    if (requestEditor.isModified()) {
      String content = requestEditor.getContents().toString();
      Protobuf payload = null;
      try {
        payload = ProtobufHumanConvertor.decodeFromHuman(content);
      } catch (JsonProcessingException e) {
        logging.logToError(e);
      }
      String output = ProtobufEncoder.encodeToProtobuf(payload);

      request = requestResponse.request().withBody(ByteArray.byteArray(output));
    } else {
      request = requestResponse.request();
    }

    return request;
  }

  @Override
  public void setRequestResponse(HttpRequestResponse requestResponse) {
    this.requestResponse = requestResponse;

    ByteArray bodyValue = requestResponse.request().body();
    String body = bodyValue.toString();
    String output;

    try {
      Protobuf payload = ProtobufMessageDecoder.decodeProto(EncodingUtils.parseInput(body));
      output = ProtobufHumanConvertor.encodeToHuman(payload);
    } catch (JsonProcessingException | InsufficientResourcesException e) {
      logging.logToError(e);
      output = "Insufficient resources";
    }

    this.requestEditor.setContents(ByteArray.byteArray(output));
  }

  @Override
  public boolean isEnabledFor(HttpRequestResponse requestResponse) {
    Optional<ParsedHttpParameter> dataParam =
        requestResponse.request().parameters().stream()
            .filter(p -> p.name().equals("data"))
            .findFirst();

    dataParam.ifPresent(httpParameter -> parsedHttpParameter = httpParameter);

    String contentType =
        requestResponse.request().headers().stream()
            .filter(
                header ->
                    header.name().equals("Content-Type")
                        && header.value().contains("application/grpc-web-text"))
            .findFirst()
            .orElse(null)
            .value();

    boolean isGrpcWebText = contentType != null;

    return dataParam.isPresent() || isGrpcWebText;
  }

  @Override
  public String caption() {
    return "Protobuf Magic";
  }

  @Override
  public Component uiComponent() {
    return requestEditor.uiComponent();
  }

  @Override
  public Selection selectedData() {
    return requestEditor.selection().isPresent() ? requestEditor.selection().get() : null;
  }

  @Override
  public boolean isModified() {
    return requestEditor.isModified();
  }
}
