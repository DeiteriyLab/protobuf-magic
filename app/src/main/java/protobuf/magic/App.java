package protobuf.magic;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import lombok.CustomLog;

@CustomLog
public class App implements BurpExtension {
  @Override
  public void initialize(MontoyaApi api) {
    log.setInstance(api.logging());

    api.extension().setName(Config.extensionName());
    log.output("Loaded protobuf magic");

    var ui = api.userInterface();
    ui.registerSuiteTab(Config.extensionName(), DecoderTabFactory.create(api));
    ui.registerHttpRequestEditorProvider(new ProtobufHttpRequestEditorProvider(api));
    ui.registerHttpResponseEditorProvider(new ProtobufHttpResponseEditorProvider(api));
    ui.registerContextMenuItemsProvider(new ProtobufContextMenuItemsProvider(api));
    api.intruder().registerPayloadGeneratorProvider(new ProtobufPayloadGeneratorProvider());
    api.intruder().registerPayloadProcessor(new ProtobufPayloadProcessor(api));
    api.http().registerHttpHandler(new ProtobufHttpHandler(api));
  }
}
