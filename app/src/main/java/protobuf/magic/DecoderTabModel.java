package protobuf.magic;

import burp.api.montoya.http.handler.HttpResponseReceived;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DecoderTabModel extends AbstractTableModel {
  private final List<HttpResponseReceived> log;

  public DecoderTabModel() {
    this.log = new ArrayList<>();
  }

  @Override
  public synchronized int getRowCount() {
    return log.size();
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public String getColumnName(int column) {
    return switch (column) {
      case 0 -> "Tool";
      case 1 -> "URL";
      default -> "";
    };
  }

  @Override
  public synchronized Object getValueAt(int rowIndex, int columnIndex) {
    HttpResponseReceived responseReceived = log.get(rowIndex);

    return switch (columnIndex) {
      case 0 -> responseReceived.toolSource().toolType();
      case 1 -> responseReceived.initiatingRequest().url();
      default -> "";
    };
  }

  public synchronized void add(HttpResponseReceived responseReceived) {
    int index = log.size();
    log.add(responseReceived);
    fireTableRowsInserted(index, index);
  }

  public synchronized HttpResponseReceived get(int rowIndex) {
    return log.get(rowIndex);
  }
}
