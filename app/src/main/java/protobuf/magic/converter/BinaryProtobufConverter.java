package protobuf.magic.converter;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protobuf.dynamic.MessageDefinition;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InsufficientResourcesException;
import lombok.CustomLog;
import protobuf.magic.exception.UnknownTypeException;
import protobuf.magic.protobuf.BufferReader;
import protobuf.magic.struct.DynamicProtobuf;
import protobuf.magic.struct.Field;
import protobuf.magic.struct.Type;

@CustomLog
public class BinaryProtobufConverter
    extends Converter<List<Byte>, DynamicProtobuf> {
  private static String INVALID = "INVALID";

  public BinaryProtobufConverter() {
    super(BinaryProtobufConverter::wrapBytesToProtobuf,
          BinaryProtobufConverter::protobufToBytes);
  }

  private static List<DynamicProtobuf> splitProtobuf(DynamicProtobuf proto) {
    if (proto.fields().size() <= 1) {
      return List.of(proto);
    }

    var leftOver = proto.leftOver();
    List<DynamicProtobuf> res = new ArrayList<>();
    DynamicProtobuf currentProto =
        new DynamicProtobuf(new ArrayList<>(), new byte[0]);
    List<Integer> seenIndexes = new ArrayList<>();

    for (var field : proto.fields()) {
      if (!seenIndexes.contains(field.index())) {
        currentProto.fields().add(
            new Field(field.index(), field.type(), field.value()));
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
      DynamicProtobuf lastProto =
          new DynamicProtobuf(currentProto.fields(), leftOver);
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
    DynamicMessage.Builder msgBuilder =
        schema.newMessageBuilder("DynamicSchema");
    Descriptor msgDesc = msgBuilder.getDescriptorForType();
    for (Field field : res.fields()) {
      String fieldName = generateKey(field.index());
      Object value = mapperValue(field.type(), field.value());
      FieldDescriptor fieldDesc = msgDesc.findFieldByName(fieldName);
      if (field.type() == Type.VARINT) {
        value = Long.parseLong((String)value);
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

    MessageDefinition.Builder msgDefBuilder =
        MessageDefinition.newBuilder("DynamicSchema");

    for (Field field : res.fields()) {
      String fieldName = generateKey(field.index());
      Type fieldType = field.type();
      msgDefBuilder.addField("optional", getProtobufFieldType(fieldType),
                             fieldName, field.index());
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
      return ((String)value).getBytes();
    case I64:
      return Long.parseLong(new String((byte[])value));
    case I32:
      return Integer.parseInt(new String((byte[])value));
    case SGROUP:
    case EGROUP:
      return value;
    default:
      return value;
    }
  }

  private static DynamicProtobuf wrapBytesToProtobuf(List<Byte> bufferList) {
    try {
      return bytesToProtobuf(bufferList);
    } catch (InsufficientResourcesException | UnknownTypeException e) {
      log.error(e);
      return new DynamicProtobuf(List.of(new Field(0, Type.LEN, INVALID)),
                                 new byte[0]);
    }
  }

  private static DynamicProtobuf bytesToProtobuf(List<Byte> bufferList)
      throws InsufficientResourcesException, UnknownTypeException {
    byte[] buffer = toArray(bufferList);
    BufferReader reader = new BufferReader(buffer);
    List<Field> parts = new ArrayList<>();

    reader.trySkipGrpcHeader();
    processBuffer(reader, parts);

    byte[] leftover = reader.readBuffer(reader.leftBytes());
    return new DynamicProtobuf(parts, leftover);
  }

  private static byte[] toArray(List<Byte> bufferList) {
    byte[] buffer = new byte[bufferList.size()];
    for (int i = 0; i < bufferList.size(); i++) {
      buffer[i] = bufferList.get(i);
    }
    return buffer;
  }

  private static void processBuffer(BufferReader reader, List<Field> parts)
      throws UnknownTypeException, InsufficientResourcesException {
    while (reader.leftBytes() > 0) {
      reader.checkpoint();
      int indexType = reader.readVarInt().intValue();
      int type = indexType & 0b111;
      int index = indexType >> 3;

      String value = readValueBasedOnType(reader, type);
      parts.add(new Field(index, Type.fromValue(type), value));
    }
  }

  private static String readValueBasedOnType(BufferReader reader, int type)
      throws UnknownTypeException, InsufficientResourcesException {
    Type fieldType = Type.fromValue(type);
    return switch (fieldType) {
      case VARINT -> reader.readVarInt().toString();
      case LEN -> new String(reader.readBuffer(reader.readVarInt().intValue()));
      case I32 -> new String(reader.readBuffer(4));
      case I64 -> new String(reader.readBuffer(8));
      default -> throw new UnknownTypeException("Unknown type: " + fieldType.getName());
    };
  }

  public static List<Byte> protobufToBytes(DynamicProtobuf res) {
    var parts = splitProtobuf(res);
    List<Byte> resBytes = new ArrayList<>();
    for (var part : parts) {
      resBytes.addAll(toList(encodeToProtobuf(part)));
    }
        return resBytes;
      }

      private static List<Byte> toList(byte[] bytes) {
        List<Byte> byteList = new ArrayList<>();
        for (byte b : bytes) {
          byteList.add(b);
        }
        return byteList;
      }
  }
