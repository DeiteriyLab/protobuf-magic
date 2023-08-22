package protobuf.magic;

import static burp.api.montoya.intruder.PayloadProcessingResult.usePayload;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.intruder.PayloadData;
import burp.api.montoya.intruder.PayloadProcessingResult;
import burp.api.montoya.intruder.PayloadProcessor;
import javax.naming.InsufficientResourcesException;
import protobuf.magic.protobuf.ProtobufMessageDecoder;

public class ProtobufPayloadProcessor implements PayloadProcessor {
  private final MontoyaApi api;
  private final Logger logging = new Logger(ProtobufPayloadProcessor.class);

  public ProtobufPayloadProcessor(MontoyaApi api) {
    this.api = api;
  }

  @Override
  public String displayName() {
    return "Protobuf Magic";
  }

  @Override
  public PayloadProcessingResult processPayload(PayloadData payloadData) {
    String input = payloadData.currentPayload().toString();
    byte[] bytes = EncodingUtils.parseInput(input);
    String output;
    try {
      var protobuf = ProtobufMessageDecoder.decodeProto(bytes);
      output = ProtobufJsonConverter.encodeToJson(protobuf).toString();
    } catch (InsufficientResourcesException e) {
      logging.logToError(e);
      output = "Insufficient resources";
    }
    return usePayload(ByteArray.byteArray(output));
  }
}
