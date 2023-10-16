package protobuf.magic.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.naming.InsufficientResourcesException;
import org.junit.jupiter.api.Test;
import protobuf.magic.EncodingUtils;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Type;

class ProtoDecoderTest {

  @Test
  void testDecodeEmptyProto() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("");
    DynamicProtobuf result = MessageDecoder.decodeProto(buffer);

    assertEquals(0, result.fields().size());
    assertEquals(0, result.leftOver().length);
  }

  @Test
  void testDecodeEmptyGrpc() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("00 00000000");
    DynamicProtobuf result = MessageDecoder.decodeProto(buffer);

    assertEquals(0, result.fields().size());
    assertEquals(0, result.leftOver().length);
  }

  @Test
  void testDecodeInt() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("089601");
    DynamicProtobuf result = MessageDecoder.decodeProto(buffer);

    assertEquals(1, result.fields().size());
    Field part = result.fields().get(0);
    assertEquals(Type.VARINT, part.type());
    assertEquals(1, part.index());
    assertEquals("150", part.value());
    //    assertEquals(0, part.getByteRange()[0]);
    //    assertEquals(3, part.getByteRange()[1]);
    assertEquals(0, result.leftOver().length);
  }

  @Test
  void testDecodeString() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("12 07 74 65 73 74 69 6e 67");
    DynamicProtobuf result = MessageDecoder.decodeProto(buffer);

    assertEquals(1, result.fields().size());
    Field part = result.fields().get(0);
    assertEquals(Type.LEN, part.type());
    assertEquals(2, part.index());
    assertEquals("testing", part.value());
    //    assertEquals(0, part.getByteRange()[0]);
    //    assertEquals(9, part.getByteRange()[1]);
    assertEquals(0, result.leftOver().length);
  }

  @Test
  void testDecodeIntAndString() throws InsufficientResourcesException {
    byte[] buffer = EncodingUtils.parseInput("08 96 01 12 07 74 65 73 74 69 6e 67");
    DynamicProtobuf result = MessageDecoder.decodeProto(buffer);

    assertEquals(2, result.fields().size());

    Field part1 = result.fields().get(0);
    assertEquals(Type.VARINT, part1.type());
    assertEquals(1, part1.index());
    assertEquals("150", part1.value());
    //    assertEquals(0, part1.getByteRange()[0]);
    //    assertEquals(3, part1.getByteRange()[1]);

    Field part2 = result.fields().get(1);
    assertEquals(Type.LEN, part2.type());
    assertEquals(2, part2.index());
    assertEquals("testing", part2.value());
    //    assertEquals(3, part2.getByteRange()[0]);
    //    assertEquals(12, part2.getByteRange()[1]);

    assertEquals(0, result.leftOver().length);
  }
}
