package protobuf.magic.logger;

public class LoggerFactory {
  public static Logger createLogger(String topic, Class<?> clazz) {
    String className =
        String.format("%s.%s.%s", topic, clazz.getPackage().getName(), clazz.getName());
    return new Logger(className);
  }

  public static Logger createLogger(Class<?> clazz) {
    String className = String.format("%s.%s", clazz.getPackage().getName(), clazz.getName());
    return new Logger(className);
  }
}
