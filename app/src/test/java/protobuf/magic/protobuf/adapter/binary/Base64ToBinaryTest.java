package protobuf.magic.protobuf.adapter.binary;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import protobuf.magic.adapter.binary.*;

public class Base64ToBinaryTest {
  StringToBinary convert = new Base64ToBinary();

  @Test
  void testParseBase64() throws Exception {
    List<Byte> result = convert.convert("qgIBAA==");
    List<Byte> expected = Arrays.asList((byte) 0xAA, (byte) 0x02, (byte) 0x01, (byte) 0x00);
    assertIterableEquals(expected, result);
  }
}
