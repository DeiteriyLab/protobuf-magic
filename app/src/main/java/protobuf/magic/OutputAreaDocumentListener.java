package protobuf.magic;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import protobuf.magic.converter.Converter;
import protobuf.magic.converter.HumanReadableToBinary;

public class OutputAreaDocumentListener implements DocumentListener {
  private final JTextArea outputArea;
  private final JTextArea inputArea;
  private LockActions lockActions = new LockActions();
  private Converter<String, String> converter = new HumanReadableToBinary();

  public OutputAreaDocumentListener(JTextArea outputArea, JTextArea inputArea) {
    this.outputArea = outputArea;
    this.inputArea = inputArea;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    if (lockActions.isLock()) return;
    lockActions.setLock(true);
    String input = converter.convertFromDTO(outputArea.getText());
    inputArea.setText(input);
    lockActions.setLock(false);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    insertUpdate(e);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {}
}
