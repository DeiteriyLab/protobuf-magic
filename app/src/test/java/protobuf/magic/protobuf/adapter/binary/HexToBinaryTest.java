package protobuf.magic.protobuf.adapter.binary;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import protobuf.magic.adapter.binary.*;

public class HexToBinaryTest {
  StringToBinary convert = new HexToBinary();

  @Test
  void testParseLowercasedHex() throws Exception {
    List<Byte> result = convert.convert("aabbcc");
    List<Byte> expected = Arrays.asList((byte) 0xAA, (byte) 0xBB, (byte) 0xCC);
    assertIterableEquals(expected, result);
  }

  @Test
  void testParseUppercasedHex() throws Exception {
    List<Byte> result = convert.convert("AABBCC");
    List<Byte> expected = Arrays.asList((byte) 0xAA, (byte) 0xBB, (byte) 0xCC);
    assertIterableEquals(expected, result);
  }

  @Test
  void testParseHexWithSpacesAndNewlines() throws Exception {
    List<Byte> result = convert.convert("aa bb\ncc\tdd");
    List<Byte> expected = Arrays.asList((byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD);
    assertIterableEquals(expected, result);
  }

  @Test
  void testParseHexWithPrefix() throws Exception {
    List<Byte> result = convert.convert("0xaa 0xbb 0xcc 0xdd");
    List<Byte> expected = Arrays.asList((byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD);
    assertIterableEquals(expected, result);
  }
}
