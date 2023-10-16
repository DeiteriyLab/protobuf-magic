package protobuf.magic;

import java.util.List;
import lombok.experimental.UtilityClass;
import protobuf.magic.converter.BinaryProtobufConverter;
import protobuf.magic.converter.Converter;
import protobuf.magic.converter.JsonConverter;
import protobuf.magic.struct.DynamicProtobuf;

@UtilityClass
public class Config {
  public static Converter<String, DynamicProtobuf> convertHumanReadableProtobuf() {
    return new JsonConverter();
  }

  public static Converter<List<Byte>, DynamicProtobuf> convertBinaryProtobuf() {
    return new BinaryProtobufConverter();
  }

  public static String extensionName() {
    return "Protobuf Magic";
  }
}
