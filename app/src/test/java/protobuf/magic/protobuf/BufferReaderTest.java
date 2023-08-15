package protobuf.magic.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.naming.InsufficientResourcesException;
import org.junit.jupiter.api.Test;
import protobuf.magic.EncodingUtils;
import protobuf.magic.struct.Protobuf;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;

class ProtoDecoderTest {

  @Test
  void testDecodeEmptyProto() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("");
    Protobuf result = ProtobufMessageDecoder.decodeProto(buffer);

    assertEquals(0, result.getProtobufFields().size());
    assertEquals(0, result.getLeftOver().length);
  }

  @Test
  void testDecodeEmptyGrpc() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("00 00000000");
    Protobuf result = ProtobufMessageDecoder.decodeProto(buffer);

    assertEquals(0, result.getProtobufFields().size());
    assertEquals(0, result.getLeftOver().length);
  }

  @Test
  void testDecodeInt() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("089601");
    Protobuf result = ProtobufMessageDecoder.decodeProto(buffer);

    assertEquals(1, result.getProtobufFields().size());
    ProtobufField part = result.getProtobufFields().get(0);
    assertEquals(ProtobufFieldType.VARINT, part.getType());
    assertEquals(1, part.getIndex());
    assertEquals("150", part.getValue());
    assertEquals(0, part.getByteRange()[0]);
    assertEquals(3, part.getByteRange()[1]);
    assertEquals(0, result.getLeftOver().length);
  }

  @Test
  void testDecodeString() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("12 07 74 65 73 74 69 6e 67");
    Protobuf result = ProtobufMessageDecoder.decodeProto(buffer);

    assertEquals(1, result.getProtobufFields().size());
    ProtobufField part = result.getProtobufFields().get(0);
    assertEquals(ProtobufFieldType.LENDELIM, part.getType());
    assertEquals(2, part.getIndex());
    assertEquals("testing", part.getValue());
    assertEquals(0, part.getByteRange()[0]);
    assertEquals(9, part.getByteRange()[1]);
    assertEquals(0, result.getLeftOver().length);
  }

  @Test
  void testDecodeIntAndString() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("08 96 01 12 07 74 65 73 74 69 6e 67");
    Protobuf result = ProtobufMessageDecoder.decodeProto(buffer);

    assertEquals(2, result.getProtobufFields().size());

    ProtobufField part1 = result.getProtobufFields().get(0);
    assertEquals(ProtobufFieldType.VARINT, part1.getType());
    assertEquals(1, part1.getIndex());
    assertEquals("150", part1.getValue());
    assertEquals(0, part1.getByteRange()[0]);
    assertEquals(3, part1.getByteRange()[1]);

    ProtobufField part2 = result.getProtobufFields().get(1);
    assertEquals(ProtobufFieldType.LENDELIM, part2.getType());
    assertEquals(2, part2.getIndex());
    assertEquals("testing", part2.getValue());
    assertEquals(3, part2.getByteRange()[0]);
    assertEquals(12, part2.getByteRange()[1]);

    assertEquals(0, result.getLeftOver().length);
  }
}
