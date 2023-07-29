package protobuf.magic;

import java.util.List;

public class DecodeResult {
  List<Part> parts;
  byte[] leftOver;

  DecodeResult(List<Part> parts, byte[] leftOver) {
    this.parts = parts;
    this.leftOver = leftOver;
  }
}
