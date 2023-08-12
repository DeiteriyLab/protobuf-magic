package protobuf.magic;

import javax.naming.InsufficientResourcesException;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InputAreaDocumentListener implements DocumentListener {
  private static final Logger logging = new Logger(InputAreaDocumentListener.class);
  private final JTextArea inputArea;
  private final JTextArea outputArea;
  private final boolean[] isUpdating;

  public InputAreaDocumentListener(
      JTextArea inputArea, JTextArea outputArea, boolean[] isUpdating) {
    this.inputArea = inputArea;
    this.outputArea = outputArea;
    this.isUpdating = isUpdating;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    if (isUpdating[0]) return;
    isUpdating[0] = true;
    SwingUtilities.invokeLater(
        () -> {
          String input = inputArea.getText();
          byte[] bytes = EncodingUtils.parseInput(input);
          String output;
          try {
            var protobuf = ProtobufMessageDecoder.decodeProto(bytes);
            output = ProtobufJsonConverter.encodeToJson(protobuf).toString();
          } catch (InsufficientResourcesException ex) {
            logging.logToError(ex);
            output = "Insufficient resources";
          }
          outputArea.setText(output);
          isUpdating[0] = false;
        });
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    insertUpdate(e);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {}
}
