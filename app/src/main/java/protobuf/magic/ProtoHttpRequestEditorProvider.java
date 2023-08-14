package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.extension.EditorCreationContext;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedHttpRequestEditor;
import burp.api.montoya.ui.editor.extension.HttpRequestEditorProvider;

class ProtoHttpRequestEditorProvider implements HttpRequestEditorProvider
{
    private final MontoyaApi api;

    ProtoHttpRequestEditorProvider(MontoyaApi api)
    {
        this.api = api;
    }

    @Override
    public ExtensionProvidedHttpRequestEditor provideHttpRequestEditor(EditorCreationContext creationContext)
    {
        return new ProtoExtensionProvidedHttpRequestEditor(api, creationContext);
    }
}
