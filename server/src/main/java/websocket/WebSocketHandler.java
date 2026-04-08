package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import dataaccess.*;
import model.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


@WebSocket
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
        try {
            AuthData auth = AuthDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                return error("error: unauthorized");
            }

            String username = auth.username();
            GameData game = gameService.getGame(command.getGameID());

            if (game == null) {
                return error("error: game not found");
            }

            ConnectionManager.add(command.getGameID(), username, this.session);

            ServerMessage loadMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadMsg.game = game;
            session.getRemote().sendString(gson.toJson(loadMsg));

            String role = getRole(username, game);

            ServerMessage notification =
                    new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);

            notification.message = username + " joined as " + role;

            connectionManager.broadcastExcept(
                    command.getGameID(),
                    username,
                    gson.toJson(notification)
            );

            return null;
        } catch (Exception e) {
            return error("error: " + e.getMessage());
        }

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
