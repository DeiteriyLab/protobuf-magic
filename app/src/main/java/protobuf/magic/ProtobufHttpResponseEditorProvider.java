package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpResponseEditor;
import burp.api.montoya.ui.editor.extension.HttpResponseEditorProvider;

class ProtobufHttpResponseEditorProvider implements HttpResponseEditorProvider {
  private final MontoyaApi api;

  ProtobufHttpResponseEditorProvider(MontoyaApi api) {
    this.api = api;
  }

  @Override
  public ExtensionProvidedHttpResponseEditor provideHttpResponseEditor(
      EditorCreationContext creationContext) {
    return new ProtobufExtensionProvidedHttpResponseEditor(api, creationContext);
  }
}
