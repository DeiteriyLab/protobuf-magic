package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.awt.Component;
import java.nio.charset.Charset;
import java.util.List;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.protobuf.ProtobufEncoder;
import protobuf.magic.protobuf.ProtobufMessageDecoder;
import protobuf.magic.struct.Protobuf;

class ProtobufExtensionProvidedHttpRequestEditor
    implements ExtensionProvidedHttpRequestEditor {
  private final Logger logging =
      new Logger(ProtobufExtensionProvidedHttpRequestEditor.class);
  private final RawEditor requestEditor;
  private HttpRequestResponse requestResponse;

  ProtobufExtensionProvidedHttpRequestEditor(
      MontoyaApi api, EditorCreationContext creationContext) {
    if (creationContext.editorMode() == EditorMode.READ_ONLY) {
      requestEditor =
          api.userInterface().createRawEditor(EditorOptions.READ_ONLY);
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
      Protobuf payload =
          ProtobufMessageDecoder.decodeProto(EncodingUtils.parseInput(body));
      output = ProtobufHumanConvertor.encodeToHuman(payload);
    } catch (JsonProcessingException | InsufficientResourcesException e) {
      logging.logToError(e);
      output = "Insufficient resources";
    }

    this.requestEditor.setContents(ByteArray.byteArray(output));
  }

  @Override
  public boolean isEnabledFor(HttpRequestResponse requestResponse) {
    List<HttpHeader> headers = null;
    if (requestResponse.request() != null) {
      headers = requestResponse.request().headers();
    } else if (requestResponse.response() != null) {
      headers = requestResponse.response().headers();
    }

    if (headers == null || headers.size() == 0) {
      return false;
    }

    return headers.stream()
        .filter(h
                -> h.name().equalsIgnoreCase("Content-Type") &&
                       h.value().contains("application/grpc-web-text"))
        .findFirst()
        .isPresent();
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
    return requestEditor.selection().isPresent()
        ? requestEditor.selection().get()
        : null;
  }

  @Override
  public boolean isModified() {
    return requestEditor.isModified();
  }
}
