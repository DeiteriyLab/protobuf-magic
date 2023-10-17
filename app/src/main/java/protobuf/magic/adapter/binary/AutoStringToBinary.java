package protobuf.magic.adapter.binary;

import java.util.List;
import lombok.CustomLog;
import protobuf.magic.exception.UnknownStructException;

@CustomLog
public class AutoStringToBinary implements StringToBinary {
  private final List<StringToBinary> converters = List.of(new Base64ToBinary(), new HexToBinary());

  @Override
  public List<Byte> convert(String str) throws UnknownStructException {
    for (StringToBinary converter : converters) {
      try {
        return converter.convert(str);
      } catch (UnknownStructException e) {
        log.debug(e.getMessage());
      }
    }
    throw new UnknownStructException("No suitable converter found for the given string");
  }
}
