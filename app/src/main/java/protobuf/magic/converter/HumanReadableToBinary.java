package protobuf.magic.converter;

import java.util.Base64;
import lombok.CustomLog;
import protobuf.magic.Config;
import protobuf.magic.handler.Base64Handler;
import protobuf.magic.handler.ByteHandler;
import protobuf.magic.handler.EmptyHandler;
import protobuf.magic.handler.HexHandler;
import protobuf.magic.struct.DynamicProtobuf;

@CustomLog
public class HumanReadableToBinary extends Converter<String, String> {
  private static final ByteHandler BYTE_HANDLER =
      new HexHandler().setNext(new Base64Handler().setNext(new EmptyHandler()));

  public HumanReadableToBinary() {
    super(HumanReadableToBinary::wrapHumanToBinary, HumanReadableToBinary::wrapBinaryToHuman);
  }

  private static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  private static String wrapBinaryToHuman(String str) {
    return isEmpty(str) ? "" : binaryToHuman(str);
  }

  private static String wrapHumanToBinary(String str) {
    return isEmpty(str) ? "" : humanToBinary(str);
  }

  private static String binaryToHuman(String str) {
    var binary = BYTE_HANDLER.handle(str);
    log.debug(String.format("binaryToHuman get binary %s from %s", binary, str));
    DynamicProtobuf proto = Config.convertBinaryProtobuf().convertFromDTO(binary);
    log.debug(String.format("binaryToHuman get proto %s", proto));
    String humanReadable = Config.convertHumanReadableProtobuf().convertFromEntity(proto);
    log.debug(String.format("binaryToHuman get humanReadable %s", humanReadable));

    return humanReadable;
  }

  private static String humanToBinary(String human) {
    log.debug(String.format("humanToBinary %s", human));
    DynamicProtobuf proto = Config.convertHumanReadableProtobuf().convertFromDTO(human);
    var humanReadable = Config.convertBinaryProtobuf().convertFromEntity(proto);
    byte[] copy = new byte[humanReadable.size()];
    for (int i = 0; i < humanReadable.size(); i++) {
      copy[i] = humanReadable.get(i);
    }
    return Base64.getEncoder().encodeToString(copy);
  }
}
