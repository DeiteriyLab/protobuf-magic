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
    assertEquals(TYPES.VARINT, part.getType());
    assertEquals(1, part.getIndex());
    assertEquals("150", part.getValue());
    assertEquals(0, part.getByteRange()[0]);
    assertEquals(3, part.getByteRange()[1]);
    assertEquals(0, result.leftOver.length);
  }

  @Test
  void testDecodeString() {
    byte[] buffer = BufferUtils.parseInput("12 07 74 65 73 74 69 6e 67");
    DecodeResult result = ProtoDecoder.decodeProto(buffer);

    assertEquals(1, result.parts.size());
    Part part = result.parts.get(0);
    assertEquals(TYPES.LENDELIM, part.getType());
    assertEquals(2, part.getIndex());
    assertEquals("testing", part.getValue());
    assertEquals(0, part.getByteRange()[0]);
    assertEquals(9, part.getByteRange()[1]);
    assertEquals(0, result.leftOver.length);
  }

  @Test
  void testDecodeIntAndString() {
    byte[] buffer = BufferUtils.parseInput("08 96 01 12 07 74 65 73 74 69 6e 67");
    DecodeResult result = ProtoDecoder.decodeProto(buffer);

    assertEquals(2, result.parts.size());

    Part part1 = result.parts.get(0);
    assertEquals(TYPES.VARINT, part1.getType());
    assertEquals(1, part1.getIndex());
    assertEquals("150", part1.getValue());
    assertEquals(0, part1.getByteRange()[0]);
    assertEquals(3, part1.getByteRange()[1]);

    Part part2 = result.parts.get(1);
    assertEquals(TYPES.LENDELIM, part2.getType());
    assertEquals(2, part2.getIndex());
    assertEquals("testing", part2.getValue());
    assertEquals(3, part2.getByteRange()[0]);
    assertEquals(12, part2.getByteRange()[1]);

    assertEquals(0, result.leftOver.length);
  }
}
