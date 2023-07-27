package protobuf.magic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BufferUtilsTest {

  @Test
  void testParseLowercasedHex() {
    byte[] result = BufferUtils.parseInput("aabbcc");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC}, result);
  }

  @Test
  void testParseUppercasedHex() {
    byte[] result = BufferUtils.parseInput("AABBCC");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC}, result);
  }

  @Test
  void testParseHexWithSpacesAndNewlines() {
    byte[] result = BufferUtils.parseInput("aa bb\ncc\tdd");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD}, result);
  }

  @Test
  void testParseHexWithPrefix() {
    byte[] result = BufferUtils.parseInput("0xaa 0xbb 0xcc 0xdd");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD}, result);
  }

  @Test
  void testBufferToPrettyHex() {
    byte[] input = {(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0xAA};
    String result = BufferUtils.bufferToPrettyHex(input);
    assertEquals("00 01 02 aa", result);
  }

  @Test
  void testBufferLeToBeHex() {
    byte[] input = {(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0xAA};
    String result = BufferUtils.bufferLeToBeHex(input);
    assertEquals("aa020100", result);
  }

  @Test
  void testParseBase64() {
    byte[] result = BufferUtils.parseInput("qgIBAA==");
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0x02, (byte) 0x01, (byte) 0x00}, result);
  }
}
