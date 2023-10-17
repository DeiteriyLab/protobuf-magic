package protobuf.magic.adapter;

import protobuf.magic.exception.*;

public interface Converter<T, U> {
  public T convert(U u) throws UnknownStructException;
}
