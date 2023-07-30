package protobuf.magic;

import burp.api.montoya.http.handler.*;

public class MHttpHandler implements HttpHandler {
  private final DecoderTabModel tableModel;

  public MHttpHandler(DecoderTabModel tableModel) {

    this.tableModel = tableModel;
  }

  @Override
  public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
    return RequestToBeSentAction.continueWith(requestToBeSent);
  }

  @Override
  public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
    tableModel.add(responseReceived);
    return ResponseReceivedAction.continueWith(responseReceived);
  }
}
