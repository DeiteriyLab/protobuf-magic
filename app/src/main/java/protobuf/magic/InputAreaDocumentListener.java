package protobuf.magic;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.naming.InsufficientResourcesException;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import protobuf.magic.protobuf.ProtobufMessageDecoder;

public class InputAreaDocumentListener implements DocumentListener {
  private static final Logger logging = new Logger(InputAreaDocumentListener.class);
  private final JTextArea inputArea;
  private final JTextArea outputArea;
  private LockActions lockActions = new LockActions();

  public InputAreaDocumentListener(JTextArea inputArea, JTextArea outputArea) {
    this.inputArea = inputArea;
    this.outputArea = outputArea;
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    if (lockActions.isLock()) return;
    lockActions.setLock(true);
    String input = inputArea.getText();
    byte[] bytes = new byte[0];
    try {
      bytes = EncodingUtils.parseInput(input);
    } catch (StringIndexOutOfBoundsException ex) {
      logging.logToError(ex);
    }
    String output;
    try {
      var protobuf = ProtobufMessageDecoder.decodeProto(bytes);
      output = ProtobufHumanConvertor.encodeToHuman(protobuf);
    } catch (JsonProcessingException | InsufficientResourcesException ex) {
      logging.logToError(ex);
      output = "Insufficient resources";
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
