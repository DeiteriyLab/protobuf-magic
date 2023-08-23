package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class DecoderTabFactory {
  private static JTextArea inputArea = new JTextArea();
  private static JTextArea outputArea = new JTextArea();

  public static JTextArea getInputArea() {
    return inputArea;
  }

  public static JTextArea getOutputArea() {
    return outputArea;
  }

  public static Component create(MontoyaApi api) {
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    var inputListener = new InputAreaDocumentListener(inputArea, outputArea);
    inputArea.getDocument().addDocumentListener(inputListener);

    var outputListener = new OutputAreaDocumentListener(outputArea, inputArea);
    outputArea.getDocument().addDocumentListener(outputListener);

    splitPane.setTopComponent(new JScrollPane(inputArea));
    splitPane.setBottomComponent(new JScrollPane(outputArea));

    splitPane.setDividerLocation(100);

    return splitPane;
  }
}
