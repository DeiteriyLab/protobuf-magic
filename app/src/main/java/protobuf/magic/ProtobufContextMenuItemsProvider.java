package protobuf.magic;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;

public class ProtobufContextMenuItemsProvider implements ContextMenuItemsProvider {
  private final MontoyaApi api;

  public ProtobufContextMenuItemsProvider(MontoyaApi api) {
    this.api = api;
  }

  @Override
  public List<Component> provideMenuItems(ContextMenuEvent event) {
    if (event.isFromTool(ToolType.PROXY, ToolType.DECODER, ToolType.REPEATER, ToolType.COMPARER)) {
      List<Component> menuItemList = new ArrayList<>();

      HttpRequestResponse requestResponse =
          event.messageEditorRequestResponse().isPresent()
              ? event.messageEditorRequestResponse().get().requestResponse()
              : event.selectedRequestResponses().get(0);

      JMenuItem sendItem = new JMenuItem("Send to decode");
      String body =
          requestResponse.request() != null
              ? requestResponse.request().body().toString()
              : requestResponse.response().body().toString();
      sendItem.addActionListener(l -> DecoderTabFactory.getInputArea().setText(body));
      menuItemList.add(sendItem);

      return menuItemList;
    }

    return null;
  }
}
