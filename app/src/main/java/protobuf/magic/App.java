package protobuf.magic;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class App implements BurpExtension {
  final Logger logging = new Logger(App.class);
  static final String EXTENSION_NAME = "Protobuf Magic";

  @Override
  public void initialize(MontoyaApi api) {
    logging.setInstance(api.logging());

    api.extension().setName(EXTENSION_NAME);
    logging.logToOutput("Loaded protobuf magic");

    api.userInterface().registerSuiteTab(EXTENSION_NAME, DecoderTabFactory.create(api));
    api.userInterface()
        .registerHttpRequestEditorProvider(new ProtobufHttpRequestEditorProvider(api));
    api.userInterface()
        .registerHttpResponseEditorProvider(new ProtobufHttpResponseEditorProvider(api));
    api.userInterface().registerContextMenuItemsProvider(new ProtobufContextMenuItemsProvider(api));
    api.intruder().registerPayloadGeneratorProvider(new ProtobufPayloadGeneratorProvider());
    api.intruder().registerPayloadProcessor(new ProtobufPayloadProcessor(api));
    api.http().registerHttpHandler(new ProtobufHttpHandler(api));
  }
}
