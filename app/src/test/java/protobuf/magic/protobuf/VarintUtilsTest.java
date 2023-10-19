package protobuf.magic.protobuf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import protobuf.magic.adapter.binary.AutoStringToBinary;
import protobuf.magic.adapter.binary.StringToBinary;
import protobuf.magic.adapter.importer.BinaryToProtobuf;
import protobuf.magic.exception.UnknownStructException;
import protobuf.magic.struct.VarintResult;

class VarintUtilsTest {
  StringToBinary convert = new AutoStringToBinary();
  BinaryToProtobuf protobuf = new BinaryToProtobuf();

  @Test
  void testDecodeVarint() throws UnknownStructException {
    String input = "AC 02";
    List<Byte> list = convert.convert(input);
    byte[] buffer = new byte[list.size()];
    for (int i = 0; i < list.size(); i++) {
      buffer[i] = list.get(i);
    }

    VarintResult expectedOutput1 = new VarintResult(300L, 2);
    assertTrue(
        expectedOutput1.equals(VarintUtils.decodeVarint(buffer, 0)), "Compare buffer1 and buffer2");

    VarintResult expectedOutput2 = new VarintResult(2L, 1);
    assertTrue(
        expectedOutput2.equals(VarintUtils.decodeVarint(buffer, 1)), "Compare buffer1 and buffer2");

    String input2 = "AC AC";
    List<Byte> list2 = convert.convert(input2);
    byte[] buffer2 = new byte[list2.size()];
    for (int i = 0; i < list2.size(); i++) {
      buffer[i] = list2.get(i);
    }
    assertThrows(IndexOutOfBoundsException.class, () -> VarintUtils.decodeVarint(buffer2, 3));
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
      Long input = Long.valueOf(testCase[0]);
      Long expectedOutput = Long.valueOf(testCase[1]);
      assertEquals(expectedOutput, VarintUtils.interpretAsSignedType(input));
    }
  }
}
