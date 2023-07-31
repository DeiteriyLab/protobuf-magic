package protobuf.magic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtoDecoderUtilsTest {

  @Test
  public void decodeFixed32() {
    byte[] buf = EncodingUtils.parseInput("A4709D3F");
    assertEquals("1.2300000190734863", ProtoDecoderUtils.decodeFixed32(buf)[2].getValue());

    ProtobufFieldValue[] result =
        ProtoDecoderUtils.decodeFixed32(EncodingUtils.parseInput("00943577"));
    assertEquals("2000000000", result[0].getValue());
    assertEquals(null, result[1].getValue());

    result = ProtoDecoderUtils.decodeFixed32(EncodingUtils.parseInput("006CCA88"));
    assertEquals("-2000000000", result[0].getValue());
    assertEquals("2294967296", result[1].getValue());
  }

  @Test
  public void decodeFixed64() {
    byte[] buf = EncodingUtils.parseInput("AE47E17A14AEF33F");
    assertEquals("1.23", ProtoDecoderUtils.decodeFixed64(buf)[2].getValue());

    buf = EncodingUtils.parseInput("000084E2506CE67C");
    ProtobufFieldValue[] result = ProtoDecoderUtils.decodeFixed64(buf);
    assertEquals("9000000000000000000", result[0].getValue());
    assertEquals(null, result[1].getValue());

    buf = EncodingUtils.parseInput("00007C1DAF931983");
    result = ProtoDecoderUtils.decodeFixed64(buf);
    assertEquals("-9000000000000000000", result[0].getValue());
    assertEquals("9446744073709551616", result[1].getValue());
  }

  @Test
  public void decodeVarintParts() {
    ProtobufFieldValue[] result = ProtoDecoderUtils.decodeVarintParts(new BigInteger("1642911").toByteArray());
    assertEquals("1642911", result[0].getValue());
    assertEquals("-821456", result[1].getValue());
  }

  @Test
  public void decodeStringOrBytes() {
    ProtobufFieldValue result =
        ProtoDecoderUtils.decodeStringOrBytes(
            "normal ascii input".getBytes(StandardCharsets.UTF_8));
    assertEquals("normal ascii input", result.getValue());
    assertEquals(ProtobufFieldType.STRING, result.getType());

    result = ProtoDecoderUtils.decodeStringOrBytes(new byte[] {0, -128, -1});
    assertEquals("Byte representation", result.getValue());
    assertEquals(ProtobufFieldType.BYTES, result.getType());

    result = ProtoDecoderUtils.decodeStringOrBytes(new byte[] {});
    assertEquals("", result.getValue());
  }
}
