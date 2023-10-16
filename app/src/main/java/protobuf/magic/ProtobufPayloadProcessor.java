package protobuf.magic;

import static burp.api.montoya.intruder.PayloadProcessingResult.usePayload;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.intruder.PayloadData;
import burp.api.montoya.intruder.PayloadProcessingResult;
import burp.api.montoya.intruder.PayloadProcessor;
import lombok.CustomLog;
import protobuf.magic.converter.Converter;
import protobuf.magic.converter.HumanReadableToBinary;

@CustomLog
public class ProtobufPayloadProcessor implements PayloadProcessor {
  private final MontoyaApi api;
  private static final Converter<String, String> converter = new HumanReadableToBinary();

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
    String output = converter.convertFromDTO(input);
    return usePayload(ByteArray.byteArray(output));
  }
}
