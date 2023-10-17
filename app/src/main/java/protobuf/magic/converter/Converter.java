package protobuf.magic.converter;

import java.util.List;
import java.util.function.Function;

public class Converter<T, U> {
  private final Function<T, U> fromDTO;
  private final Function<U, T> fromEntity;

  protected Converter(Function<T, U> fromDTO, Function<U, T> fromEntity) {
    this.fromDTO = fromDTO;
    this.fromEntity = fromEntity;
  }

  public U convertFromDTO(T dto) {
    return fromDTO.apply(dto);
  }

  public T convertFromEntity(U entity) {
    return fromEntity.apply(entity);
  }

  public final List<U> convertFromDTOList(List<T> dtos) {
    return dtos.stream().map(this::convertFromDTO).toList();
  }

  public final List<T> convertFromEntityList(List<U> entities) {
    return entities.stream().map(this::convertFromEntity).toList();
  }
}
