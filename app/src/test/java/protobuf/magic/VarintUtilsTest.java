package protobuf.magic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class VarintUtilsTest {

  @Test
  void testDecodeVarint() {
    String input = "AC 02";
    byte[] buffer = BufferUtils.parseInput(input);
    VarintResult expectedOutput1 = new VarintResult(BigInteger.valueOf(300), 2);
    assertTrue(
        expectedOutput1.equals(VarintUtils.decodeVarint(buffer, 0)), "Compare buffer1 and buffer2");

    VarintResult expectedOutput2 = new VarintResult(BigInteger.valueOf(2), 1);
    assertTrue(
        expectedOutput2.equals(VarintUtils.decodeVarint(buffer, 1)), "Compare buffer1 and buffer2");

    String input2 = "AC AC";
    byte[] buffer2 = BufferUtils.parseInput(input2);
    assertThrows(IndexOutOfBoundsException.class, () -> VarintUtils.decodeVarint(buffer2, 1));
  }

  @Test
  void testInterpretAsSignedType() {
    String[][] testCases = {
      {"0", "0"},
      {"1", "-1"},
      {"2", "1"},
      {"3", "-2"},
      {"4294967294", "2147483647"},
      {"4294967295", "-2147483648"},
      {"1642911", "-821456"}
    };

    for (String[] testCase : testCases) {
      BigInteger input = new BigInteger(testCase[0]);
      BigInteger expectedOutput = new BigInteger(testCase[1]);
      assertEquals(expectedOutput, VarintUtils.interpretAsSignedType(input));
    }
  }
}
