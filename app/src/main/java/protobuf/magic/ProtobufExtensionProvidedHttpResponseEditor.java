package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;
import java.awt.Component;
import java.util.Optional;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.protobuf.ProtobufEncoder;
import protobuf.magic.protobuf.ProtobufMessageDecoder;
import protobuf.magic.struct.Protobuf;

class ProtobufExtensionProvidedHttpResponseEditor implements ExtensionProvidedHttpResponseEditor {
  private final Logger logging = new Logger(ProtobufExtensionProvidedHttpResponseEditor.class);
  private final RawEditor requestEditor;
  private HttpRequestResponse requestResponse;

  ProtobufExtensionProvidedHttpResponseEditor(
      MontoyaApi api, EditorCreationContext creationContext) {
    if (creationContext.editorMode() == EditorMode.READ_ONLY) {
      requestEditor = api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
    } else {
      requestEditor = api.userInterface().createRawEditor();
    }
  }

  @Override
  public HttpResponse getResponse() {
    HttpResponse request;

    if (requestEditor.isModified()) {
      String content = requestEditor.getContents().toString();
      Protobuf payload = ProtobufHumanConvertor.decodeFromHuman(content);
      String output = ProtobufEncoder.encodeToProtobuf(payload);

      request = requestResponse.response().withBody(ByteArray.byteArray(output));
    } else {
      request = requestResponse.response();
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
