package protobuf.magic;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import protobuf.magic.protobuf.ProtobufEncoder;
import protobuf.magic.struct.Protobuf;

public class OutputAreaDocumentListener implements DocumentListener {
  private final JTextArea outputArea;
  private final JTextArea inputArea;
  private boolean isUpdating;

  public OutputAreaDocumentListener(JTextArea outputArea, JTextArea inputArea, boolean isUpdating) {
    this.outputArea = outputArea;
    this.inputArea = inputArea;
    this.isUpdating = isUpdating;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    if (isUpdating) return;
    isUpdating = true;
    SwingUtilities.invokeLater(
        () -> {
          String output = outputArea.getText();
          Protobuf res = ProtobufJsonConverter.decodeFromJson(output);
          String input = ProtobufEncoder.encodeToProtobuf(res);
          inputArea.setText(input);
          isUpdating = false;
        });
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    insertUpdate(e);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {}
}
