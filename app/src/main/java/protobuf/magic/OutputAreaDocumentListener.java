package protobuf.magic;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import protobuf.magic.adapter.HumanReadableToBinary;
import protobuf.magic.exception.UnknownStructException;

public class OutputAreaDocumentListener implements DocumentListener {
  private final JTextArea outputArea;
  private final JTextArea inputArea;
  private LockActions lockActions = new LockActions();
  private static final HumanReadableToBinary converter = new HumanReadableToBinary();

  public OutputAreaDocumentListener(JTextArea outputArea, JTextArea inputArea) {
    this.outputArea = outputArea;
    this.inputArea = inputArea;
  }

  @Override
  public void insertUpdate(DocumentEvent event) {
    if (lockActions.isLock()) return;
    lockActions.setLock(true);
    String input;
    try {
      input = converter.convert(outputArea.getText());
    } catch (UnknownStructException e) {
      input = e.getMessage();
    }
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
