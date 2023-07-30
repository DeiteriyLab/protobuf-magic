package protobuf.magic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class ProtoDecoderUtilsTest {

  @Test
  public void decodeFixed32() {
    byte[] buf = BufferUtils.parseInput("A4709D3F");
    assertEquals("1.2300000190734863", ProtoDecoderUtils.decodeFixed32(buf)[2].getValue());

    Protobuf[] result = ProtoDecoderUtils.decodeFixed32(BufferUtils.parseInput("00943577"));
    assertEquals("2000000000", result[0].getValue());
    assertEquals(null, result[1].getValue());

    result = ProtoDecoderUtils.decodeFixed32(BufferUtils.parseInput("006CCA88"));
    assertEquals("-2000000000", result[0].getValue());
    assertEquals("2294967296", result[1].getValue());
  }

  @Test
  public void decodeFixed64() {
    byte[] buf = BufferUtils.parseInput("AE47E17A14AEF33F");
    assertEquals("1.23", ProtoDecoderUtils.decodeFixed64(buf)[2].getValue());

    buf = BufferUtils.parseInput("000084E2506CE67C");
    Protobuf[] result = ProtoDecoderUtils.decodeFixed64(buf);
    assertEquals("9000000000000000000", result[0].getValue());
    assertEquals(null, result[1].getValue());

    buf = BufferUtils.parseInput("00007C1DAF931983");
    result = ProtoDecoderUtils.decodeFixed64(buf);
    assertEquals("-9000000000000000000", result[0].getValue());
    assertEquals("9446744073709551616", result[1].getValue());
  }

  @Test
  public void decodeVarintParts() {
    Protobuf[] result = ProtoDecoderUtils.decodeVarintParts(new BigInteger("1642911"));
    assertEquals("1642911", result[0].getValue());
    assertEquals("-821456", result[1].getValue());
  }

  @Test
  public void decodeStringOrBytes() {
    Protobuf result =
        ProtoDecoderUtils.decodeStringOrBytes(
            "normal ascii input".getBytes(StandardCharsets.UTF_8));
    assertEquals("normal ascii input", result.getValue());
    assertEquals(TYPES.STRING, result.getType());

    result = ProtoDecoderUtils.decodeStringOrBytes(new byte[] {0, -128, -1});
    assertEquals("Byte representation", result.getValue());
    assertEquals(TYPES.BYTES, result.getType());

    result = ProtoDecoderUtils.decodeStringOrBytes(new byte[] {});
    assertEquals("", result.getValue());
  }
}
