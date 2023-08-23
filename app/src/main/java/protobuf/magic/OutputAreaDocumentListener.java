package protobuf.magic;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import protobuf.magic.protobuf.ProtobufEncoder;
import protobuf.magic.struct.Protobuf;

public class OutputAreaDocumentListener implements DocumentListener {
  private final JTextArea outputArea;
  private final JTextArea inputArea;
  private LockActions lockActions = new LockActions();

  public OutputAreaDocumentListener(JTextArea outputArea, JTextArea inputArea) {
    this.outputArea = outputArea;
    this.inputArea = inputArea;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    if (lockActions.isLock()) return;
    lockActions.setLock(true);
    String output = outputArea.getText();
    try {
      Protobuf res = ProtobufHumanConvertor.decodeFromHuman(output);
      String input = ProtobufEncoder.encodeToProtobuf(res);
      inputArea.setText(input);
    } catch (ArrayIndexOutOfBoundsException ex) {
      System.err.println(ex);
    }
    lockActions.setLock(false);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    insertUpdate(e);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {}
}
