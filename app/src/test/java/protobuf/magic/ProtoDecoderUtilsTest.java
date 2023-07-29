package protobuf.magic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ProtoDecoderUtilsTest {

  @Test
  void testDecodeFixed32() {
    byte[] input = BufferUtils.parseInput("A4709D3F");
    String result = ProtoDecoderUtils.decodeFixed32(input);
    assertEquals("{ type: \"Int\", value: 1 }, { type: \"Float\", value: 1.23 }", result);
  }

  @Test
  void testDecodeFixed32Int32() {
    byte[] input = BufferUtils.parseInput("00943577");
    String result = ProtoDecoderUtils.decodeFixed32(input);
    assertEquals(
        "{ type: \"Int\", value: 2000000000 }, { type: \"Float\", value: 2.5E-8 }", result);
  }

  @Test
  void testDecodeFixed32Uint32() {
    byte[] input = BufferUtils.parseInput("006CCA88");
    String result = ProtoDecoderUtils.decodeFixed32(input);
    assertEquals(
        "{ type: \"Int\", value: -2000000000 }, { type: \"Unsigned Int\", value: 2294967296 }, {"
            + " type: \"Float\", value: 2.1619877E-38 }",
        result);
  }

  @Test
  void testDecodeFixed64() {
    byte[] input = BufferUtils.parseInput("AE47E17A14AEF33F");
    String result = ProtoDecoderUtils.decodeFixed64(input);
    assertEquals(
        "{ type: \"Int\", value: 49517601571415243 }, { type: \"Double\", value: 1.23 }", result);
  }

  @Test
  void testDecodeFixed64Int64() {
    byte[] input = BufferUtils.parseInput("000084E2506CE67C");
    String result = ProtoDecoderUtils.decodeFixed64(input);
    assertEquals(
        "{ type: \"Int\", value: 9000000000000000000 }, { type: \"Double\", value:"
            + " 8.98846567431158E307 }",
        result);
  }

  @Test
  void testDecodeFixed64Uint64() {
    byte[] input = BufferUtils.parseInput("00007C1DAF931983");
    String result = ProtoDecoderUtils.decodeFixed64(input);
    assertEquals(
        "{ type: \"Int\", value: -9000000000000000000 }, { type: \"Unsigned Int\", value:"
            + " 9446744073709551616 }, { type: \"Double\", value: 3.130808997871E-260 }",
        result);
  }

  @Test
  void testDecodeVarintParts() {
    String value = "1642911";
    String result = ProtoDecoderUtils.decodeVarintParts(value);
    assertEquals("{ type: \"Int\", value: 1642911 }", result);
  }

  @Test
  void testDecodeVarintPartsSignedInt() {
    String value = "-821456";
    String result = ProtoDecoderUtils.decodeVarintParts(value);
    assertEquals(
        "{ type: \"Int\", value: -821456 }, { type: \"Signed Int\", value: -821456 }", result);
  }

  @Test
  void testDecodeStringOrBytesString() {
    byte[] input = BufferUtils.parseInput("6e6f726d616c20617363696920696e707574");
    String result = ProtoDecoderUtils.decodeStringOrBytes(input);
    assertEquals("{ type: \"string\", value: \"normal ascii input\" }", result);
  }

  @Test
  void testDecodeStringOrBytesBytes() {
    byte[] input = BufferUtils.parseInput("0080FF");
    String result = ProtoDecoderUtils.decodeStringOrBytes(input);
    assertEquals("{ type: \"bytes\", value: \"00 80 ff\" }", result);
  }

  @Test
  void testDecodeStringOrBytesEmpty() {
    byte[] input = new byte[0];
    String result = ProtoDecoderUtils.decodeStringOrBytes(input);
    assertEquals("{ type: \"string|bytes\", value: \"\" }", result);
  }
}
