package protobuf.magic;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protobuf.dynamic.MessageDefinition;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;
import com.google.protobuf.util.JsonFormat.TypeRegistry;
import java.util.Base64;
import protobuf.magic.struct.ProtobufDecodingResult;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldType;

public class ProtobufEncoder {
  private static DynamicSchema createDynamicSchema(ProtobufDecodingResult res)
      throws DescriptorValidationException {
    DynamicSchema.Builder schemaBuilder = DynamicSchema.newBuilder();
    schemaBuilder.setName("DynamicSchema");

    MessageDefinition.Builder msgDefBuilder = MessageDefinition.newBuilder("DynamicSchema");

    for (ProtobufField field : res.getProtobufFields()) {
      String fieldName = field.getProtobuf().getValue();
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
    switch (type) {
      case VARINT:
        return "int32";
      case FIXED64:
        return "fixed64";
      case LENDELIM:
        return "string";
      case FIXED32:
        return "fixed32";
      case UINT:
        return "uint32";
      case FLOAT:
        return "float";
      case DOUBLE:
        return "double";
      case INT:
        return "int32";
      case SINT:
        return "sint32";
      case BYTES:
        return "bytes";
      case STRING:
      case STRING_OR_BYTES:
        return "string";
      default:
        return "string";
    }
  }

  public static String encodeToProtobuf(ProtobufDecodingResult res) {
    try {
      var schema = createDynamicSchema(res);
      DynamicMessage.Builder msgBuilder = schema.newMessageBuilder("DynamicSchema");
      Descriptor msgDesc = msgBuilder.getDescriptorForType();
      for (ProtobufField field : res.getProtobufFields()) {
        String fieldName = field.getProtobuf().getValue();
        Object value = convertValueToProtobufType(field.getType(), field.getValue());
        FieldDescriptor fieldDesc = msgDesc.findFieldByName(fieldName);
        msgBuilder.setField(fieldDesc, value);
      }
      DynamicMessage msg = msgBuilder.build();
      return encodeToBase64(msg);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static Object convertValueToProtobufType(ProtobufFieldType type, String value) {
    switch (type) {
      case VARINT:
      case FIXED64:
      case LENDELIM:
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
        return value;
      default:
        return null;
    }
  }

  private static String encodeToBase64(DynamicMessage msg) throws Exception {
    return Base64.getEncoder().encodeToString(msg.toByteArray());
  }
}
