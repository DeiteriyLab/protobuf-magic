package protobuf.magic;

import burp.api.montoya.intruder.AttackConfiguration;
import burp.api.montoya.intruder.PayloadGenerator;
import burp.api.montoya.intruder.PayloadGeneratorProvider;

public class ProtobufPayloadGeneratorProvider implements PayloadGeneratorProvider {
  @Override
  public String displayName() {
    return "Protobuf Magic";
  }

  @Override
  public PayloadGenerator providePayloadGenerator(AttackConfiguration attackConfiguration) {
    return new ProtobufPayloadGenerator();
  }
}
