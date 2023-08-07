package protobuf.magic;

import com.google.gson.Gson;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Parser;
import java.util.Base64;
import protobuf.magic.struct.ProtobufDecodingResult;
import protobuf.magic.struct.ProtobufField;
import protobuf.magic.struct.ProtobufFieldValue;

public class ProtobufEncoder {
  private static Parser jsonFormat = JsonFormat.parser();
  private static Gson gson = new Gson();

  public static String encodeToProtobuf(ProtobufDecodingResult res) {
    String json = gson.toJson(res);
    var descriptor = createDescriptor(res);
    DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
    try {
      jsonFormat.merge(json, builder);
      DynamicMessage message = builder.build();
      byte[] byteArr = message.toByteArray();
      return Base64.getEncoder().encodeToString(byteArr);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static Descriptor createDescriptor(ProtobufDecodingResult res) {

    DescriptorProto.Builder messageBuilder =
        DescriptorProto.newBuilder().setName("ProtobufDecodingResult");

    int fieldNumber = 1;
    for (ProtobufField field : res.getProtobufFields()) {

      ProtobufFieldValue fieldValue = field.getProtobuf();

      FieldDescriptorProto.Type fieldType;
      switch (fieldValue.getType()) {
        case STRING:
          fieldType = FieldDescriptorProto.Type.TYPE_STRING;
          break;
        case INT:
          fieldType = FieldDescriptorProto.Type.TYPE_INT32;
          break;
        case LENDELIM:
          fieldType = FieldDescriptorProto.Type.TYPE_BYTES;
          break;
        case BYTES:
          fieldType = FieldDescriptorProto.Type.TYPE_BYTES;
          break;
        case DOUBLE:
          fieldType = FieldDescriptorProto.Type.TYPE_DOUBLE;
          break;
        case FIXED32:
          fieldType = FieldDescriptorProto.Type.TYPE_FIXED32;
          break;
        case FIXED64:
          fieldType = FieldDescriptorProto.Type.TYPE_FIXED64;
          break;
        case UINT:
          fieldType = FieldDescriptorProto.Type.TYPE_UINT32;
          break;
        case SINT:
          fieldType = FieldDescriptorProto.Type.TYPE_SINT32;
          break;

        default:
          throw new IllegalArgumentException("Unsupported field type: " + fieldValue.getType());
      }

      FieldDescriptorProto fieldProto =
          FieldDescriptorProto.newBuilder()
              .setName(fieldValue.getValue().toString())
              .setNumber(fieldNumber++)
              .setType(fieldType)
              .build();

      messageBuilder.addField(fieldProto);
    }

    FileDescriptorProto file =
        FileDescriptorProto.newBuilder()
            .setName("dynamic.proto")
            .addMessageType(messageBuilder.build())
            .build();

    FileDescriptor fileDescriptor;
    try {
      fileDescriptor = FileDescriptor.buildFrom(file, new FileDescriptor[0]);
    } catch (Descriptors.DescriptorValidationException e) {
      e.printStackTrace();
      return null;
    }

    return fileDescriptor.getMessageTypes().get(0);
  }
}
