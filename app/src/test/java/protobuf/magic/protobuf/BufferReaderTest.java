package protobuf.magic.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import protobuf.magic.adapter.binary.AutoStringToBinary;
import protobuf.magic.adapter.binary.StringToBinary;
import protobuf.magic.adapter.importer.BinaryToProtobuf;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Type;

class ProtoDecoderTest {
  StringToBinary convert = new AutoStringToBinary();
  BinaryToProtobuf protobuf = new BinaryToProtobuf();

  @Test
  void testDecodeEmptyProto() throws Exception {
    List<Byte> buffer = convert.convert("");
    DynamicProtobuf result = protobuf.convert(buffer);

    assertEquals(0, result.fields().size());
    assertEquals(0, result.leftOver().length);
  }

  @Test
  void testDecodeEmptyGrpc() throws Exception {
    List<Byte> buffer = convert.convert("00 00000000");
    DynamicProtobuf result = protobuf.convert(buffer);

    assertEquals(0, result.fields().size());
    assertEquals(0, result.leftOver().length);
  }

  @Test
  void testDecodeInt() throws Exception {
    List<Byte> buffer = convert.convert("089601");
    DynamicProtobuf result = protobuf.convert(buffer);

    assertEquals(1, result.fields().size());
    Field part = result.fields().get(0);
    assertEquals(Type.VARINT, part.type());
    assertEquals(1, part.index());
    assertEquals(150L, part.value().asLong());
    //    assertEquals(0, part.getByteRange()[0]);
    //    assertEquals(3, part.getByteRange()[1]);
    assertEquals(0, result.leftOver().length);
  }

  @Test
  void testDecodeString() throws Exception {
    List<Byte> buffer = convert.convert("12 07 74 65 73 74 69 6e 67");
    DynamicProtobuf result = protobuf.convert(buffer);

    assertEquals(1, result.fields().size());
    Field part = result.fields().get(0);
    assertEquals(Type.LEN, part.type());
    assertEquals(2, part.index());
    assertEquals("testing", part.value().asString());
    //    assertEquals(0, part.getByteRange()[0]);
    //    assertEquals(9, part.getByteRange()[1]);
    assertEquals(0, result.leftOver().length);
  }

  @Test
  void testDecodeIntAndString() throws Exception {
    List<Byte> buffer = convert.convert("08 96 01 12 07 74 65 73 74 69 6e 67");
    DynamicProtobuf result = protobuf.convert(buffer);

    assertEquals(2, result.fields().size());

    Field part1 = result.fields().get(0);
    assertEquals(Type.VARINT, part1.type());
    assertEquals(1, part1.index());
    assertEquals(150L, part1.value().asLong());
    //    assertEquals(0, part1.getByteRange()[0]);
    //    assertEquals(3, part1.getByteRange()[1]);

    Field part2 = result.fields().get(1);
    assertEquals(Type.LEN, part2.type());
    assertEquals(2, part2.index());
    assertEquals("testing", part2.value().asString());
    //    assertEquals(3, part2.getByteRange()[0]);
    //    assertEquals(12, part2.getByteRange()[1]);

    assertEquals(0, result.leftOver().length);
  }
}
