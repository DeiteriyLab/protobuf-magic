package protobuf.magic;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lombok.CustomLog;
import protobuf.magic.converter.Converter;
import protobuf.magic.converter.HumanReadableToBinary;

@CustomLog
public class InputAreaDocumentListener implements DocumentListener {
  private final JTextArea inputArea;
  private final JTextArea outputArea;
  private LockActions lockActions = new LockActions();
  private Converter<String, String> converter = new HumanReadableToBinary();

  public InputAreaDocumentListener(JTextArea inputArea, JTextArea outputArea) {
    this.inputArea = inputArea;
    this.outputArea = outputArea;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    if (lockActions.isLock()) return;
    lockActions.setLock(true);

    String output = converter.convertFromEntity(inputArea.getText());
    outputArea.setText(output);

    lockActions.setLock(false);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    insertUpdate(e);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {}
}
