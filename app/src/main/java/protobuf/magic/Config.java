package protobuf.magic;

import lombok.experimental.UtilityClass;
import protobuf.magic.adapter.binary.AutoStringToBinary;
import protobuf.magic.adapter.binary.StringToBinary;
import protobuf.magic.adapter.exporter.ProtobufToHumanReadable;
import protobuf.magic.adapter.exporter.ProtobufToJson;
import protobuf.magic.adapter.importer.BinaryToProtobuf;
import protobuf.magic.adapter.importer.HumanReadableToProtobuf;
import protobuf.magic.adapter.importer.JsonToProtobuf;

@UtilityClass
public class Config {
  public static ProtobufToHumanReadable convertProtobufToHumanReadable() {
    return new ProtobufToJson();
  }

  public static StringToBinary convertRawStringToBinary() {
    return new AutoStringToBinary();
  }

  public static BinaryToProtobuf convertBinaryProtobuf() {
    return new BinaryToProtobuf();
  }

  public static HumanReadableToProtobuf convertHumanReadableToProtobuf() {
    return new JsonToProtobuf();
  }

  public static String extensionName() {
    return "Protobuf Magic";
  }
}
