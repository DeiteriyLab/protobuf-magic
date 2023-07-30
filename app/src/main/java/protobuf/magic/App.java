package protobuf.magic;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

public class App implements BurpExtension {
  @Override
  public void initialize(MontoyaApi api) {
    Logging logging = api.logging();

    api.extension().setName("Protobuf Magic");
    logging.logToOutput("Loaded protobuf magic");

    DecoderTabModel tabModel = new DecoderTabModel();
    api.userInterface().registerSuiteTab("Protobuf Magic", DecoderTabFactory.create(api, tabModel));
  }
}
