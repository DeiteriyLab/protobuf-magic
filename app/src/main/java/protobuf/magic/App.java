package protobuf.magic;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

public class App implements BurpExtension {
  @Override
  public void initialize(MontoyaApi api) {
    api.extension().setName("Protobuf Magic");

    Logging logging = api.logging();

    logging.logToOutput("Hello output.");
  }
}
