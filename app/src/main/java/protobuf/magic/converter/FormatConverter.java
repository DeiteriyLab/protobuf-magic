package protobuf.magic.converter;

import java.util.function.Function;
import protobuf.magic.struct.DynamicProtobuf;

public abstract class FormatConverter extends Converter<String, DynamicProtobuf> {
  public FormatConverter(
      Function<String, DynamicProtobuf> fromDTO, Function<DynamicProtobuf, String> fromEntity) {
    super(fromDTO, fromEntity);
  }
}
