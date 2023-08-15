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
import burp.api.montoya.utilities.Base64Utils;
import burp.api.montoya.utilities.URLUtils;
import java.awt.Component;
import java.util.Optional;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.struct.ProtobufDecodingResult;

class ProtoExtensionProvidedHttpRequestEditor implements ExtensionProvidedHttpRequestEditor {
  private final Logger logging = new Logger(ProtoExtensionProvidedHttpRequestEditor.class);
  private final RawEditor requestEditor;
  private final Base64Utils base64Utils;
  private final URLUtils urlUtils;
  private HttpRequestResponse requestResponse;
  private final MontoyaApi api;

  private ParsedHttpParameter parsedHttpParameter;

  ProtoExtensionProvidedHttpRequestEditor(MontoyaApi api, EditorCreationContext creationContext) {
    this.api = api;
    base64Utils = api.utilities().base64Utils();
    urlUtils = api.utilities().urlUtils();

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
      ProtobufDecodingResult payload = ProtobufJsonConverter.decodeFromJson(content);
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
      ProtobufDecodingResult payload =
          ProtobufMessageDecoder.decodeProto(EncodingUtils.parseInput(body));
      output = ProtobufJsonConverter.encodeToJson(payload);
    } catch (InsufficientResourcesException e) {
      logging.logToError(e);
      output = "Insufficient resources";
    }

    this.requestEditor.setContents(ByteArray.byteArray(output));
  }

  @Override
  public boolean isEnabledFor(HttpRequestResponse requestResponse) {
    Optional<?> reqContentTypeHeader =
        requestResponse.request().headers().stream()
            .filter(
                h ->
                    h.name().equalsIgnoreCase("Content-Type")
                        && h.value().contains("application/grpc-web-text"))
            .findFirst();

    return reqContentTypeHeader.isPresent();
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