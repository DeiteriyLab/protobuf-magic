package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import java.awt.Component;
import javax.naming.InsufficientResourcesException;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DecoderTabFactory {
  public static Component create(MontoyaApi api, DecoderTabModel tableModel) {
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    JTextArea inputArea = new JTextArea();
    JTextArea outputArea = new JTextArea();

    inputArea
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              public void insertUpdate(DocumentEvent e) {
                updateOutput();
              }

              public void removeUpdate(DocumentEvent e) {
                updateOutput();
              }

              public void changedUpdate(DocumentEvent e) {
                updateOutput();
              }

              void updateOutput() {
                String input = inputArea.getText();
                byte[] bytes = EncodingUtils.parseInput(input);
                String output;
                try {
                  output = ProtobufMessageDecoder.decodeProto(bytes).toString();
                } catch (InsufficientResourcesException e) {
                  output = "Insufficient resources";
                }
                outputArea.setText(output);
              }
            });

    splitPane.setTopComponent(new JScrollPane(inputArea));
    splitPane.setBottomComponent(new JScrollPane(outputArea));

    return splitPane;
  }
}
