package websocket;

import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebSocketHandler {
    private final Gson gson = new Gson();

    public String handleMessage(String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            return switch (command.getCommandType()) {
                case CONNECT -> handleConnect(command);
                case MAKE_MOVE -> handleMove(command);
                case LEAVE -> handleLeave(command);
                case RESIGN -> handleResign(command);
            };
        } catch (Exception e) {
            return error("error: bad request");
        }
    }

    private String handleConnect(UserGameCommand command) {
        ServerMessage msg =
                new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        return gson.toJson(msg);
    }

    private String handleMove(UserGameCommand command) {
        return error("error: not implemented");
    }

    private String handleLeave(UserGameCommand command) {
        return null;
    }

    private String handleResign(UserGameCommand command) {
        return error("error: not implemented");
    }

    private String error(String message) {
        ServerMessage msg =
                new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        msg.errorMessage = message;
        return gson.toJson(msg);
    }
}
