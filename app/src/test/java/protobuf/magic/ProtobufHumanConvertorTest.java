package protobuf.magic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import protobuf.magic.struct.Protobuf;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtobufHumanConvertorTest {
  @Test
  public void testDecodeFromHuman() {
    String human = "1:int:123\n2:float:321.0\n3:double:123.1";
    Protobuf result = ProtobufHumanConvertor.decodeFromHuman(human);
    assertNotNull(result);
    assertEquals(3, result.getProtobufFields().size());
    assertEquals("123", result.getProtobufFields().get(0).getValue());
    assertEquals("321.0", result.getProtobufFields().get(1).getValue());
    assertEquals("123.1", result.getProtobufFields().get(2).getValue());
  }

  @Test
  public void testEncodeToHuman() {
    ProtobufFieldValue protobufFieldValue3 =
        new ProtobufFieldValue(ProtobufFieldType.DOUBLE, "123.1");
    ProtobufField protobufField3 = new ProtobufField(new int[0], 2, protobufFieldValue3);
    ProtobufFieldValue protobufFieldValue2 =
        new ProtobufFieldValue(ProtobufFieldType.FLOAT, "321.0");
    ProtobufField protobufField2 = new ProtobufField(new int[0], 1, protobufFieldValue2);
    ProtobufFieldValue protobufFieldValue1 = new ProtobufFieldValue(ProtobufFieldType.INT, "123");
    ProtobufField protobufField1 = new ProtobufField(new int[0], 0, protobufFieldValue1);

    List<ProtobufField> protobufFields =
        Arrays.asList(protobufField1, protobufField2, protobufField3);
    Protobuf result = new Protobuf(protobufFields, new byte[0], 0);

    String shuman = ProtobufHumanConvertor.encodeToHuman(result).toString();

    assertEquals("1:INT:123\n2:FLOAT:321.0\n3:DOUBLE:123.1\n0:leftOver:0", shuman);
  }
}
