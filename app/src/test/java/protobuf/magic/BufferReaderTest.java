package protobuf.magic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

import java.lang.reflect.Field;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class BufferReaderTest {

  @Test
  void testReadVarInt() {
    byte[] buffer = BufferUtils.parseInput("8F01");
    BufferReader reader = new BufferReader(buffer);

    BigInteger result = reader.readVarInt();
    assertEquals(143, result);

    assertEquals(2, reader.getOffset());
  }

  @Test
  void testReadBuffer() {
    byte[] buffer = BufferUtils.parseInput("AABBCCDD");
    BufferReader reader = new BufferReader(buffer);

    byte[] result = reader.readBuffer(3);
    assertArrayEquals(new byte[] {(byte) 0xAA, (byte) 0xBB, (byte) 0xCC}, result);

    assertEquals(3, reader.getOffset());
  }

  @Test
  void testTrySkipGrpcHeader() {
    byte[] buffer = BufferUtils.parseInput("0000000000");
    BufferReader reader = new BufferReader(buffer);

    reader.trySkipGrpcHeader();

    assertEquals(5, reader.getOffset());
  }

  @Test
  void testLeftBytes() {
    byte[] buffer = BufferUtils.parseInput("AABBCC");
    BufferReader reader = new BufferReader(buffer);

    int leftBytes = reader.leftBytes();
    assertEquals(3, leftBytes);
  }

  @Test
  void testCheckpointAndResetToCheckpoint() {
    byte[] buffer = BufferUtils.parseInput("AABBCC");
    BufferReader reader = new BufferReader(buffer);

    reader.checkpoint();
    reader.readBuffer(2);

    assertEquals(2, reader.getOffset());

    reader.resetToCheckpoint();

    assertEquals(0, reader.getOffset());
  }

  @Test
  void testGetOffsetWithSpy() throws Exception {
    byte[] buffer = BufferUtils.parseInput("AABBCC");
    BufferReader reader = spy(new BufferReader(buffer));

    int offset = reader.getOffset();

    // Access the private field 'offset' using reflection
    Field offsetField = BufferReader.class.getDeclaredField("offset");
    offsetField.setAccessible(true);
    int privateOffset = (int) offsetField.get(reader);

    assertEquals(privateOffset, offset);
  }
}
