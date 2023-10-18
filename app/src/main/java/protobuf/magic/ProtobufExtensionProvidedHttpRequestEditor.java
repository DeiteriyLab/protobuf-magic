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
import java.awt.Component;
import java.util.Optional;
import lombok.CustomLog;
import protobuf.magic.adapter.BinaryToHumanReadable;
import protobuf.magic.adapter.HumanReadableToBinary;
import protobuf.magic.exception.UnknownStructException;

@CustomLog
class ProtobufExtensionProvidedHttpRequestEditor implements ExtensionProvidedHttpRequestEditor {
  private final RawEditor requestEditor;
  private static final BinaryToHumanReadable binaryToHuman = new BinaryToHumanReadable();
  private static final HumanReadableToBinary humanToBinary = new HumanReadableToBinary();
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
      log.info("Http request editor has changed: " + content);
      String output = content;
      try {
        output = humanToBinary.convert(content);
      } catch (UnknownStructException e) {
        log.error(e);
      }

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
    String output = body;

    log.info("Http request editor has changed: " + body);
    try {
      output = binaryToHuman.convert(body);
    } catch (UnknownStructException e) {
      log.error(e);
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

    if (requestResponse.request().headers().size() == 0) {
      return false;
    }

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
    return Config.extensionName();
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
