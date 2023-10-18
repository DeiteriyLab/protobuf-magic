package protobuf.magic;

import burp.api.montoya.intruder.GeneratedPayload;
import burp.api.montoya.intruder.IntruderInsertionPoint;
import burp.api.montoya.intruder.PayloadGenerator;

public class ProtobufPayloadGenerator implements PayloadGenerator {
  @Override
  public GeneratedPayload generatePayloadFor(IntruderInsertionPoint insertionPoint) {
    return GeneratedPayload.payload(insertionPoint.baseValue().toString());
  }
}
