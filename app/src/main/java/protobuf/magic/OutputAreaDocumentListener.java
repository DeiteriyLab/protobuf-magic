package protobuf.magic;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class OutputAreaDocumentListener implements DocumentListener {
  private final JTextArea outputArea;
  private final JTextArea inputArea;
  private final boolean[] isUpdating;

  public OutputAreaDocumentListener(
      JTextArea outputArea, JTextArea inputArea, boolean[] isUpdating) {
    this.outputArea = outputArea;
    this.inputArea = inputArea;
    this.isUpdating = isUpdating;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    if (isUpdating[0]) return;
    isUpdating[0] = true;
    SwingUtilities.invokeLater(
        () -> {
          String output = outputArea.getText();
          String input = "meme";
          inputArea.setText(input);
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
