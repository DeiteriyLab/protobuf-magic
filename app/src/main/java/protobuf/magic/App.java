package protobuf.magic;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class App implements BurpExtension {
  final Logger logging = new Logger(App.class);

  @Override
  public void initialize(MontoyaApi api) {
    logging.setInstance(api.logging());

    api.extension().setName("Protobuf Magic");
    logging.logToOutput("Loaded protobuf magic");

    DecoderTabModel tabModel = new DecoderTabModel();
    api.userInterface().registerSuiteTab("Protobuf Magic", DecoderTabFactory.create(api, tabModel));
  }
}
