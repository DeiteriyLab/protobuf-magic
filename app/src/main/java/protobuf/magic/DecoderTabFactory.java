package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class DecoderTabFactory {
  public static Component create(MontoyaApi api, DecoderTabModel tableModel) {
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    JTextArea inputArea = new JTextArea();
    JTextArea outputArea = new JTextArea();

    final boolean[] isUpdating = {false}; // TODO: fix that hack

    inputArea
        .getDocument()
        .addDocumentListener(new InputAreaDocumentListener(inputArea, outputArea, isUpdating));
    outputArea
        .getDocument()
        .addDocumentListener(new OutputAreaDocumentListener(outputArea, inputArea, isUpdating));

    splitPane.setTopComponent(new JScrollPane(inputArea));
    splitPane.setBottomComponent(new JScrollPane(outputArea));

    return splitPane;
  }
}
