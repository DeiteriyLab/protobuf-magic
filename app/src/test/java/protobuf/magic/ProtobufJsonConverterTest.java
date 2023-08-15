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

public class ProtobufJsonConverterTest {
  @Test
  public void testDecodeFromJson() {
    String jsonString = "{ \"int\": \"123\", \"float\": \"321.0\", \"double\": \"123.1\" }";

    Protobuf result = ProtobufJsonConverter.decodeFromJson(jsonString);

    assertNotNull(result);
    assertEquals(3, result.getProtobufFields().size());
    assertEquals("123", result.getProtobufFields().get(0).getValue());
    assertEquals("321.0", result.getProtobufFields().get(1).getValue());
    assertEquals("123.1", result.getProtobufFields().get(2).getValue());
  }

  @Test
  public void testEncodeToJson() {
    ProtobufFieldValue protobufFieldValue3 =
        new ProtobufFieldValue(ProtobufFieldType.DOUBLE, "123.1");
    ProtobufField protobufField3 = new ProtobufField(new int[0], 0, protobufFieldValue3);
    ProtobufFieldValue protobufFieldValue2 =
        new ProtobufFieldValue(ProtobufFieldType.FLOAT, "321.0");
    ProtobufField protobufField2 = new ProtobufField(new int[0], 0, protobufFieldValue2);
    ProtobufFieldValue protobufFieldValue1 = new ProtobufFieldValue(ProtobufFieldType.INT, "123");
    ProtobufField protobufField1 = new ProtobufField(new int[0], 0, protobufFieldValue1);

    List<ProtobufField> protobufFields =
        Arrays.asList(protobufField1, protobufField2, protobufField3);
    Protobuf result = new Protobuf(protobufFields, new byte[0], 0);

    String jsonString = ProtobufJsonConverter.encodeToJson(result).toString();
    String normalize = jsonString.replaceAll("\n", "").replaceAll(" ", "");

    assertNotNull(jsonString);
    assertEquals(
        "{\"int\":\"123\",\"float\":\"321.0\",\"double\":\"123.1\",\"leftOver\":\"0\"}", normalize);
  }
}
