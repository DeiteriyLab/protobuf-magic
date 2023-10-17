package protobuf.magic.adapter;

import protobuf.magic.Config;
import protobuf.magic.adapter.binary.StringToBinary;
import protobuf.magic.adapter.exporter.ProtobufToHumanReadable;
import protobuf.magic.adapter.importer.BinaryToProtobuf;
import protobuf.magic.exception.UnknownStructException;

public class BinaryToHumanReadable implements Converter<String, String> {
  private static final StringToBinary stringToBinary = Config.convertRawStringToBinary();
  private static final BinaryToProtobuf binaryToProtobuf = Config.convertBinaryProtobuf();
  private static final ProtobufToHumanReadable protobufToHumanReadable =
      Config.convertProtobufToHumanReadable();

  @Override
  public String convert(String str) throws UnknownStructException {
    return protobufToHumanReadable.convert(binaryToProtobuf.convert(stringToBinary.convert(str)));
  }
}
