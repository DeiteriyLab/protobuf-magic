package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import java.awt.Component;
import javax.naming.InsufficientResourcesException;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DecoderTabFactory {
  public static Component create(MontoyaApi api, DecoderTabModel tableModel) {
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    JTextArea inputArea = new JTextArea();
    JTextArea outputArea = new JTextArea();

    final boolean[] isUpdating = {false};

    inputArea
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
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
            });

    outputArea
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
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
            });

    splitPane.setTopComponent(new JScrollPane(inputArea));
    splitPane.setBottomComponent(new JScrollPane(outputArea));

    return splitPane;
  }
}
