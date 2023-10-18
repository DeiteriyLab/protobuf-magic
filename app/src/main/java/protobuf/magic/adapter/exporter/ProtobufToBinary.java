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
      Object value = field.parseValue();
      FieldDescriptor fieldDesc = msgDesc.findFieldByName(fieldName);
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
      msgDefBuilder.addField("optional", field.type().getName(), fieldName, field.index());
    }

    MessageDefinition msgDef = msgDefBuilder.build();
    schemaBuilder.addMessageDefinition(msgDef);
    return schemaBuilder.build();
  }

  private static String generateKey(int base) {
    return "i" + Integer.toHexString(base);
  }

  public static List<Byte> protobufToBytes(DynamicProtobuf proto) {
    var parts = splitProtobuf(proto);
    log.info(String.format("Split into %d parts", parts.size()));
    List<Byte> bytes = new ArrayList<>();
    for (var part : parts) {
      bytes.addAll(toList(encodeToProtobuf(part)));
    }
    if (proto.fields().size() > 0) {
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
