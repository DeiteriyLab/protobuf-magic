package protobuf.magic.struct;

import java.util.List;

public record DynamicProtobuf(List<Field> fields, byte[] leftOver) {}
