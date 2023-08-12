package protobuf.magic;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protobuf.dynamic.MessageDefinition;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import java.util.Base64;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.struct.ProtobufDecodingResult;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;

public class ProtobufEncoder {
  static final Logger logging = new Logger(ProtobufEncoder.class);

  private static DynamicSchema createDynamicSchema(ProtobufDecodingResult res)
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
          field.getIndex() + 1);
    }

    MessageDefinition msgDef = msgDefBuilder.build();
    schemaBuilder.addMessageDefinition(msgDef);
    return schemaBuilder.build();
  }

  private static String getProtobufFieldLabel(ProtobufFieldType type) {
    switch (type) {
      case VARINT:
      case FIXED64:
      case LENDELIM:
      case FIXED32:
      case UINT:
      case INT:
      case SINT:
        return "optional";
      case FLOAT:
      case DOUBLE:
        return "optional";
      case BYTES:
      case STRING:
      case STRING_OR_BYTES:
        return "optional";
      default:
        return "optional";
    }
  }

  private static String getProtobufFieldType(ProtobufFieldType type) {
    // double -> TYPE_DOUBLE
    // float -> TYPE_FLOAT
    // int32 -> TYPE_INT32
    // int64 -> TYPE_INT64
    // uint32 -> TYPE_UINT32
    // uint64 -> TYPE_UINT64
    // sint32 -> TYPE_SINT32
    // sint64 -> TYPE_SINT64
    // fixed32 -> TYPE_FIXED32
    // fixed64 -> TYPE_FIXED64
    // sfixed32 -> TYPE_SFIXED32
    // sfixed64 -> TYPE_SFIXED64
    // bool -> TYPE_BOOL
    // string -> TYPE_STRING
    // bytes -> TYPE_BYTES
    // enum -> TYPE_ENUM
    // message -> TYPE_MESSAGE
    // group -> TYPE_GROUP

    // TODO: add support 64 bit
    switch (type) {
      case VARINT:
        return "int32";
      case LENDELIM:
        return "string";
      case FIXED32:
        return "fixed32";
      case FIXED64:
        return "fixed64";
      case UINT:
        return "uint32";
      case FLOAT:
        return "float32";
      case DOUBLE:
        return "double";
      case INT:
        return "int32";
      case SINT:
        return "sint32";
      case BYTES:
        return "bytes";
      case STRING:
        return "string";
      case STRING_OR_BYTES:
        return "bytes";
      default:
        return "int32";
    }
  }

  private static String generateKey(int base) {
    return "pseudo_" + Integer.toHexString(base);
  }

  public static String encodeToProtobuf(ProtobufDecodingResult res) {
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
        logging.logToError(e.toString());
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
    switch (field.getType()) {
      case VARINT:
      case FIXED64:
      case FIXED32:
      case UINT:
      case FLOAT:
      case DOUBLE:
      case INT:
      case SINT:
        return true;
      default:
        return false;
    }
  }

  private static Object convertValueToProtobufType(ProtobufFieldType type, String value)
      throws UnknownTypeException {
    switch (type) {
      case VARINT:
      case FIXED64:
      case FIXED32:
      case UINT:
      case FLOAT:
      case DOUBLE:
      case INT:
      case SINT:
        return value;
      case BYTES:
        return ByteString.copyFrom(Base64.getDecoder().decode(value));
      case STRING:
      case STRING_OR_BYTES:
      case LENDELIM:
        return value;
      default:
        throw new UnknownTypeException(type.toString());
    }
  }

  private static String encodeToBase64(byte[] msg) {
    return Base64.getEncoder().encodeToString(msg);
  }
}
