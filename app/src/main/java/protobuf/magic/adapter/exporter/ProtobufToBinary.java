package protobuf.magic.adapter.exporter;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protobuf.dynamic.MessageDefinition;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import java.util.ArrayList;
import java.util.List;
import lombok.CustomLog;
import protobuf.magic.adapter.Converter;
import protobuf.magic.exception.UnknownStructException;
import protobuf.magic.protobuf.OffsetBytesAppender;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Type;

@CustomLog
public class ProtobufToBinary implements Converter<List<Byte>, DynamicProtobuf> {
  @Override
  public List<Byte> convert(DynamicProtobuf protobuf) throws UnknownStructException {
    return protobufToBytes(protobuf);
  }

  private static List<DynamicProtobuf> splitProtobuf(DynamicProtobuf proto) {
    if (proto.fields().size() <= 1) {
      return List.of(proto);
    }

    var leftOver = proto.leftOver();
    List<DynamicProtobuf> res = new ArrayList<>();
    DynamicProtobuf currentProto = new DynamicProtobuf(new ArrayList<>(), new byte[0]);
    List<Integer> seenIndexes = new ArrayList<>();

    for (var field : proto.fields()) {
      if (!seenIndexes.contains(field.index())) {
        currentProto.fields().add(field);
        seenIndexes.add(field.index());
      } else {
        if (!currentProto.fields().isEmpty()) {
          res.add(currentProto);
        }
        currentProto = new DynamicProtobuf(new ArrayList<>(), new byte[0]);
        currentProto.fields().add(field);
        seenIndexes.clear();
        seenIndexes.add(field.index());
      }
    }

    if (!currentProto.fields().isEmpty()) {
      DynamicProtobuf lastProto = new DynamicProtobuf(currentProto.fields(), leftOver);
      res.add(lastProto);
    }
    return res;
  }

  private static byte[] encodeToProtobuf(DynamicProtobuf res) {
    DynamicSchema schema;
    try {
      schema = createDynamicSchema(res);
    } catch (DescriptorValidationException e) {
      log.error(e);
      return new byte[0];
    }
    DynamicMessage.Builder msgBuilder = schema.newMessageBuilder("DynamicSchema");
    Descriptor msgDesc = msgBuilder.getDescriptorForType();
    for (Field field : res.fields()) {
      String fieldName = generateKey(field.index());
      Object value = mapperValue(field.type(), field.value());
      FieldDescriptor fieldDesc = msgDesc.findFieldByName(fieldName);
      if (field.type() == Type.VARINT) {
        try {
          value = Long.parseLong((String) value);
        } catch (NumberFormatException e) {
          value = 0L;
          log.error(e);
        }
      }
      msgBuilder.setField(fieldDesc, value);
    }
    DynamicMessage msg = msgBuilder.buildPartial();
    return msg.toByteArray();
  }

  private static DynamicSchema createDynamicSchema(DynamicProtobuf res)
      throws DescriptorValidationException {
    DynamicSchema.Builder schemaBuilder = DynamicSchema.newBuilder();
    schemaBuilder.setName("DynamicSchema");

    MessageDefinition.Builder msgDefBuilder = MessageDefinition.newBuilder("DynamicSchema");

    for (Field field : res.fields()) {
      String fieldName = generateKey(field.index());
      Type fieldType = field.type();
      msgDefBuilder.addField("optional", getProtobufFieldType(fieldType), fieldName, field.index());
    }

    MessageDefinition msgDef = msgDefBuilder.build();
    schemaBuilder.addMessageDefinition(msgDef);
    return schemaBuilder.build();
  }

  private static String getProtobufFieldType(Type type) {
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
      default:
        return "unknown";
    }
  }

  private static String generateKey(int base) {
    return "i" + Integer.toHexString(base);
  }

  private static Object mapperValue(Type type, Object value) {
    switch (type) {
      case VARINT:
        return value;
      case LEN:
        return ((String) value).getBytes();
      case I64:
        return Long.parseLong(new String((byte[]) value));
      case I32:
        return Integer.parseInt(new String((byte[]) value));
      case SGROUP:
      case EGROUP:
        return value;
      default:
        return value;
    }
  }

  public static List<Byte> protobufToBytes(DynamicProtobuf proto) {
    var parts = splitProtobuf(proto);
    List<Byte> bytes = new ArrayList<>();
    for (var part : parts) {
      bytes.addAll(toList(encodeToProtobuf(part)));
    }
    if (proto.fields().size() != 0) {
      return OffsetBytesAppender.append(proto.fields().get(0).byterange().start(), bytes);
    } else {
      return bytes;
    }
  }

  private static List<Byte> toList(byte[] bytes) {
    List<Byte> byteList = new ArrayList<>();
    for (byte b : bytes) {
      byteList.add(b);
    }
    return byteList;
  }
}
