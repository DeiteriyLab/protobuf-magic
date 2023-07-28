package protobuf.magic;

public class Part {
  int[] byteRange;
  int index;
  int type;
  Object value;

  Part(int[] byteRange, int index, int type, Object value) {
    this.byteRange = byteRange;
    this.index = index;
    this.type = type;
    this.value = value;
  }
}
