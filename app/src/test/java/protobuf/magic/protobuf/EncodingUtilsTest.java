package protobuf.magic.protobuf;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import protobuf.magic.EncodingUtils;

public class EncodingUtilsTest {

  @Test
  void testParseLowercasedHex() {
    byte[] result = EncodingUtils.parseInput("aabbcc");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC}, result);
  }

  @Test
  void testParseUppercasedHex() {
    byte[] result = EncodingUtils.parseInput("AABBCC");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC}, result);
  }

  @Test
  void testParseHexWithSpacesAndNewlines() {
    byte[] result = EncodingUtils.parseInput("aa bb\ncc\tdd");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD}, result);
  }

  @Test
  void testParseHexWithPrefix() {
    byte[] result = EncodingUtils.parseInput("0xaa 0xbb 0xcc 0xdd");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD}, result);
  }

  @Test
  void testBufferToPrettyHex() {
    byte[] input = {(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0xAA};
    String result = EncodingUtils.bufferToPrettyHex(input);
    assertEquals("00 01 02 aa", result);
  }

  @Test
  void testBufferToHex() {
    byte[] input = {(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0xAA};
    String result = EncodingUtils.bufferToHex(input);
    assertEquals("aa020100", result);
  }

  @Test
  void testParseBase64() {
    byte[] result = EncodingUtils.parseInput("qgIBAA==");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0x02, (byte) 0x01, (byte) 0x00}, result);
  }
}
