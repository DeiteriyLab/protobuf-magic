package protobuf.magic.protobuf;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protobuf.dynamic.MessageDefinition;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import java.util.Base64;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.struct.Protobuf;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;

public class ProtobufEncoder {
  private static DynamicSchema createDynamicSchema(Protobuf res)
      throws DescriptorValidationException {
    DynamicSchema.Builder schemaBuilder = DynamicSchema.newBuilder();
    schemaBuilder.setName("DynamicSchema");

    MessageDefinition.Builder msgDefBuilder = MessageDefinition.newBuilder("DynamicSchema");

    for (ProtobufField field : res.getProtobufFields()) {
      String fieldName = generateKey(field.getIndex());
      ProtobufFieldType fieldType = field.getType();
      msgDefBuilder.addField(
          getProtobufFieldLabel(fieldType),
          getProtobufFieldType(fieldType),
          fieldName,
          field.getIndex());
    }

    MessageDefinition msgDef = msgDefBuilder.build();
    schemaBuilder.addMessageDefinition(msgDef);
    return schemaBuilder.build();
  }

  private static String getProtobufFieldLabel(ProtobufFieldType type) {
    return "optional";
  }

  private static String getProtobufFieldType(ProtobufFieldType type) {
    // 0	VARINT	int32, int64, uint32, uint64, sint32, sint64, bool, enum
    // 1	I64	fixed64, sfixed64, double
    // 2	LEN	string, bytes, embedded messages, packed repeated fields
    // 3	SGROUP	group start (deprecated)
    // 4	EGROUP	group end (deprecated)
    // 5	I32	fixed32, sfixed32, float
    switch (type) {
      case VARINT:
        return "sint64";
      case I64:
        return "fixed64";
      case LEN:
        return "bytes";
      case SGROUP:
      case EGROUP:
        return "group"; // @FIXME
      case I32:
        return "sfixed32";
    }
    throw new RuntimeException("Unknown type: " + type);
  }

  private static String generateKey(int base) {
    return "pseudo_" + Integer.toHexString(base);
  }

  public static String encodeToProtobuf(Protobuf res) {
    DynamicSchema schema;
    try {
      schema = createDynamicSchema(res);
    } catch (DescriptorValidationException e) {
      throw new RuntimeException(e);
    }
    DynamicMessage.Builder msgBuilder = schema.newMessageBuilder("DynamicSchema");
    Descriptor msgDesc = msgBuilder.getDescriptorForType();
    for (ProtobufField field : res.getProtobufFields()) {
      String fieldName = generateKey(field.getIndex());
      Object value;
      try {
        value = convertValueToProtobufType(field.getType(), field.getValue());
      } catch (UnknownTypeException e) {
        continue;
      }
      FieldDescriptor fieldDesc = msgDesc.findFieldByName(fieldName);
      if (isDigit(field)) {
        value = Integer.parseInt(value.toString());
      }
      msgBuilder.setField(fieldDesc, value);
    }
    DynamicMessage msg = msgBuilder.build();
    byte[] msgBytes = msg.toByteArray();
    msgBytes = LeftOverBytesAppender.appendLeftOverBytes(res.getLenLeftOver(), msgBytes);
    return encodeToBase64(msgBytes);
  }

  private static boolean isDigit(final ProtobufField field) {
    return field.getType() == ProtobufFieldType.VARINT;
  }

  private static Object convertValueToProtobufType(ProtobufFieldType type, String value)
      throws UnknownTypeException {
    switch (type) {
      case VARINT:
        return value;
      case LEN:
        return ByteString.copyFrom(Base64.getDecoder().decode(value));
      case I64:
        return Long.parseLong(value);
      case I32:
        return Integer.parseInt(value);
      case SGROUP:
      case EGROUP:
        return value;
      default:
        throw new UnknownTypeException(type.toString());
    }
  }

  private static String encodeToBase64(byte[] msg) {
    return Base64.getEncoder().encodeToString(msg);
  }
}
