package protobuf.magic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ProtoDecoderTest {

  @Test
  void testDecodeEmptyProto() {
    byte[] buffer = BufferUtils.parseInput("");
    DecodeResult result = ProtoDecoder.decodeProto(buffer);

    assertEquals(0, result.parts.size());
    assertEquals(0, result.leftOver.length);
  }

  @Test
  void testDecodeEmptyGrpc() {
    byte[] buffer = BufferUtils.parseInput("00 00000000");
    DecodeResult result = ProtoDecoder.decodeProto(buffer);

    assertEquals(0, result.parts.size());
    assertEquals(0, result.leftOver.length);
  }

  @Test
  void testDecodeInt() {
    byte[] buffer = BufferUtils.parseInput("089601");
    DecodeResult result = ProtoDecoder.decodeProto(buffer);

    assertEquals(1, result.parts.size());
    Part part = result.parts.get(0);
    assertEquals(TYPES.VARINT.getValue(), part.type);
    assertEquals(1, part.index);
    assertEquals("150", part.value.toString());
    assertEquals(0, part.byteRange[0]);
    assertEquals(3, part.byteRange[1]);
    assertEquals(0, result.leftOver.length);
  }

  @Test
  void testDecodeString() {
    byte[] buffer = BufferUtils.parseInput("12 07 74 65 73 74 69 6e 67");
    DecodeResult result = ProtoDecoder.decodeProto(buffer);

    assertEquals(1, result.parts.size());
    Part part = result.parts.get(0);
    assertEquals(TYPES.LENDELIM.getValue(), part.type);
    assertEquals(2, part.index);
    assertEquals("testing", new String((byte[]) part.value));
    assertEquals(0, part.byteRange[0]);
    assertEquals(9, part.byteRange[1]);
    assertEquals(0, result.leftOver.length);
  }

  @Test
  void testDecodeIntAndString() {
    byte[] buffer = BufferUtils.parseInput("08 96 01 12 07 74 65 73 74 69 6e 67");
    DecodeResult result = ProtoDecoder.decodeProto(buffer);

    assertEquals(2, result.parts.size());

    Part part1 = result.parts.get(0);
    assertEquals(TYPES.VARINT.getValue(), part1.type);
    assertEquals(1, part1.index);
    assertEquals("150", part1.value.toString());
    assertEquals(0, part1.byteRange[0]);
    assertEquals(3, part1.byteRange[1]);

    Part part2 = result.parts.get(1);
    assertEquals(TYPES.LENDELIM.getValue(), part2.type);
    assertEquals(2, part2.index);
    assertEquals("testing", new String((byte[]) part2.value));
    assertEquals(3, part2.byteRange[0]);
    assertEquals(12, part2.byteRange[1]);

    assertEquals(0, result.leftOver.length);
  }
}
