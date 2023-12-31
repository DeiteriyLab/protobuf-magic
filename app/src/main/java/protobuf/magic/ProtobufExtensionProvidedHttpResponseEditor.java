package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.Selection;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.RawEditor;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.EditorMode;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;
import java.awt.Component;
import java.util.List;
import lombok.CustomLog;
import protobuf.magic.adapter.BinaryToHumanReadable;
import protobuf.magic.adapter.HumanReadableToBinary;
import protobuf.magic.exception.UnknownStructException;

@CustomLog
class ProtobufExtensionProvidedHttpResponseEditor implements ExtensionProvidedHttpResponseEditor {
  private final RawEditor requestEditor;
  private HttpRequestResponse requestResponse;
  private static final HumanReadableToBinary humanToBinary = new HumanReadableToBinary();
  private static final BinaryToHumanReadable binaryToHuman = new BinaryToHumanReadable();

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
      String output = content;
      try {
        output = binaryToHuman.convert(content);
      } catch (UnknownStructException e) {
        log.error(e);
      }

      request = requestResponse.response().withBody(output);
    } else {
      request = requestResponse.response();
    }

    return request;
  }

  @Override
  public void setRequestResponse(HttpRequestResponse requestResponse) {
    this.requestResponse = requestResponse;

    ByteArray bodyValue =
        requestResponse.request() != null
            ? requestResponse.request().body()
            : requestResponse.response().body();
    String body = bodyValue.toString();
    log.info("Http response editor has changed: " + body);
    String output = body;
    try {
      output = binaryToHuman.convert(body);
    } catch (UnknownStructException e) {
      log.error(e);
    }

    this.requestEditor.setContents(ByteArray.byteArray(output));
  }

  @Override
  public boolean isEnabledFor(HttpRequestResponse requestResponse) {
    List<HttpHeader> headers = null;
    if (requestResponse.response() != null) {
      headers = requestResponse.response().headers();
    } else if (requestResponse.request() != null) {
      headers = requestResponse.request().headers();
    }

    if (headers == null || headers.size() == 0) {
      return false;
    }

    return headers.stream()
        .filter(
            h ->
                h.name().equalsIgnoreCase("Content-Type")
                    && h.value().contains("application/grpc-web-text"))
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
    return requestEditor.selection().isPresent() ? requestEditor.selection().get() : null;
  }

  @Override
  public boolean isModified() {
    return requestEditor.isModified();
  }
}
