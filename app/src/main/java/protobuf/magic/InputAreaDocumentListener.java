package protobuf.magic;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import protobuf.magic.adapter.BinaryToHumanReadable;
import protobuf.magic.exception.UnknownStructException;

public class InputAreaDocumentListener implements DocumentListener {
  private final JTextArea inputArea;
  private final JTextArea outputArea;
  private LockActions lockActions = new LockActions();
  private static final BinaryToHumanReadable converter =
      new BinaryToHumanReadable();

  public InputAreaDocumentListener(JTextArea inputArea, JTextArea outputArea) {
    this.inputArea = inputArea;
    this.outputArea = outputArea;
  }

  @Override
  public void insertUpdate(DocumentEvent event) {
    if (lockActions.isLock())
      return;
    lockActions.setLock(true);
    String output;
    try {
      output = converter.convert(inputArea.getText());
    } catch (UnknownStructException e) {
      output = e.getMessage();
    }
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
