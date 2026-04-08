package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final ServerMessageHandler messageHandler;

    public WebSocketFacade(String url, ServerMessageHandler handler) throws Exception {
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");

        this.messageHandler = handler;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
            ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
            messageHandler.handle(msg);
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {}


    public void connect(String authToken, int gameID) throws IOException {
        var command = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID
        );
        session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    // (you’ll add MAKE_MOVE, LEAVE, RESIGN later)
}