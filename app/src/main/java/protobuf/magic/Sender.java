package protobuf.magic;

class Sender {
  static String body;

  Sender() {
    Sender.body = "";
  }

  static String getBody() {
    return Sender.body;
  }

  static void setBody(String body) {
    Sender.body = body;
  }
}
