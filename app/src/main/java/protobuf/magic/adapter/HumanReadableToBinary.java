package protobuf.magic.adapter;

import protobuf.magic.Config;
import protobuf.magic.adapter.binary.BinaryToBase64;
import protobuf.magic.adapter.binary.BinaryToString;
import protobuf.magic.adapter.exporter.ProtobufToBinary;
import protobuf.magic.adapter.importer.HumanReadableToProtobuf;
import protobuf.magic.exception.UnknownStructException;

public class HumanReadableToBinary implements Converter<String, String> {
  private static final BinaryToString binaryToBase64 = new BinaryToBase64();
  private static final HumanReadableToProtobuf humanReadableToProtobuf =
      Config.convertHumanReadableToProtobuf();
  private static final ProtobufToBinary protobufToBinary = new ProtobufToBinary();

  @Override
  public String convert(String str) throws UnknownStructException {
    return binaryToBase64.convert(protobufToBinary.convert(humanReadableToProtobuf.convert(str)));
  }
}
