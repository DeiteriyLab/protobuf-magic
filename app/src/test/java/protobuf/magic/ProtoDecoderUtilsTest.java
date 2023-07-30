package protobuf.magic;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class ProtoDecoderUtilsTest {

  @Test
  public void decodeFixed32() {
    ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
    buf.putInt(0xA4709D3F);
    assertEquals("1.2300000190734863", ProtoDecoderUtils.decodeFixed32(buf.array())[2].getValue());

    Proto[] result =
        ProtoDecoderUtils.decodeFixed32(
            ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(0x00943577).array());
    assertEquals("2000000000", result[0].getValue());
    assertEquals(null, result[1].getValue());

    result =
        ProtoDecoderUtils.decodeFixed32(
            ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(0x006CCA88).array());
    assertEquals(-2000000000, Integer.parseInt(result[0].getValue()));
    assertEquals(2294967296L, Long.parseLong(result[1].getValue()));
  }

  @Test
  public void decodeFixed64() {
    assertEquals(
        "1.23",
        ProtoDecoderUtils.decodeFixed64(
            ByteBuffer.allocate(8)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(0xAE47E17A14AEF33FL)
                .array())[2]
            .getValue());

    Proto[] result =
        ProtoDecoderUtils.decodeFixed64(
            ByteBuffer.allocate(8)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(0x000084E2506CE67CL)
                .array());
    assertEquals("9000000000000000000", result[0].getValue());
    assertEquals(null, result[1].getValue());

    result =
        ProtoDecoderUtils.decodeFixed64(
            ByteBuffer.allocate(8)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(0x00007C1DAF931983L)
                .array());
    assertEquals("-9000000000000000000", result[0].getValue());
    assertEquals("9446744073709551616", result[1].getValue());
  }

  @Test
  public void decodeVarintParts() {
    Proto[] result =
        ProtoDecoderUtils.decodeVarintParts(
            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(1642911).array());
    assertEquals(1642911, Integer.parseInt(result[0].getValue()));
    assertEquals(-821456, Integer.parseInt(result[1].getValue()));
  }

  @Test
  public void decodeStringOrBytes() {
    Proto result =
        ProtoDecoderUtils.decodeStringOrBytes(
            "normal ascii input".getBytes(StandardCharsets.UTF_8));
    assertEquals("normal ascii input", result.getValue());
    assertEquals("String", result.getType());

    result = ProtoDecoderUtils.decodeStringOrBytes(new byte[] {0, -128, -1});
    assertEquals("Byte representation", result.getValue());
    assertEquals("Bytes", result.getType());

    result = ProtoDecoderUtils.decodeStringOrBytes(new byte[] {});
    assertEquals("", result.getValue());
  }
}
