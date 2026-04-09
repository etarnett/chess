package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;

import org.eclipse.jetty.websocket.api.Session;

import java.time.Duration;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();

    // shared connection manager
    private final ConnectionManager connections = new ConnectionManager();

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("WebSocket connected");
        ctx.session.setIdleTimeout(Duration.ofMinutes(30));

    }

    @Override

    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command =
                    gson.fromJson(ctx.message(), UserGameCommand.class);

            Session session = ctx.session;

            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                case LEAVE -> leave(command, session);
                case MAKE_MOVE -> makeMove(command, session);
                case RESIGN -> resign(command, session);
            }

        } catch (Exception e) {
            sendError(ctx.session, "error: bad request");
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("WebSocket closed");
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "error: unauthorized");
                return;
            }

            String username = auth.username();
            int gameID = command.getGameID();

            GameData game = gameDAO.getGame(gameID);
            if (game == null) {
                sendError(session, "error: game not found");
                return;
            }

            connections.add(gameID, username, session);

            ServerMessage load =
                    new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            load.game = game.game();

            session.getRemote().sendString(gson.toJson(load));

            String color;
            if (username.equals(game.whiteUsername())) {
                color = "white";
            } else if (username.equals(game.blackUsername())) {
                color = "black";
            } else {
                color = "observer";
            }

            ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notif.message = username + " joined the game as " + color;

            connections.broadcast(gameID, username, gson.toJson(notif));
        } catch (Exception e) {
            sendError(session, "error: " + e.getMessage());
        }
    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "error: unauthorized");
                return;
            }

            String username = auth.username();
            int gameID = command.getGameID();

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "error: game not found");
                return;
            }

            String white = gameData.whiteUsername();
            String black = gameData.blackUsername();

            GameData updatedGame = gameData;

            if (username.equals(white)) {
                updatedGame = new GameData(
                        gameData.gameID(),
                        null,
                        gameData.blackUsername(),
                        gameData.gameName(),
                        gameData.game()
                );
            } if (username.equals(black)) {
                updatedGame = new GameData(
                        gameData.gameID(),
                        gameData.whiteUsername(),
                        null,
                        gameData.gameName(),
                        gameData.game()
                );
            }

            gameDAO.updateGame(updatedGame);

            connections.remove(gameID, username);

            ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notif.message = username + " left the game";

            connections.broadcast(gameID, username, gson.toJson(notif));
        } catch (Exception e) {
            sendError(session, "error: " + e.getMessage());
        }
    }

    private void makeMove(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "error: unauthorized");
                return;
            }

            String username = auth.username();
            int gameID = command.getGameID();

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "error: game not found");
                return;
            }

            var game = gameData.game();

            if (!isPlayersTurn(username, gameData)) {
                sendError(session, "error: not your turn");
                return;
            }

            if (game.isGameOver()) {
                sendError(session, "error: game is over");
                return;
            }

            try {
                game.makeMove(command.getMove());
            } catch (Exception e) {
                sendError(session, "error: " + e.getMessage());
                return; // 🔥 VERY IMPORTANT: stop execution
            }

            GameData updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            );

            gameDAO.updateGame(updatedGame);

            //load game message to everyone
            ServerMessage load =
                    new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            load.game = game;

            connections.broadcastAll(gameID, gson.toJson(load));

            //normal move notification to others
            ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notif.message = username + " made a move" + command.getMove().toString();

            connections.broadcast(gameID, username, gson.toJson(notif));

            //check/ checkmate notification to everyone
            var currentGame = updatedGame.game();

            String affectedPlayer =
                    (currentGame.getTeamTurn() == chess.ChessGame.TeamColor.WHITE)
                            ? gameData.whiteUsername()
                            : gameData.blackUsername();

            if (currentGame.isInCheckmate(currentGame.getTeamTurn())) {
                ServerMessage checkmateMsg =
                        new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                checkmateMsg.message = affectedPlayer + " is in checkmate";
                connections.broadcastAll(gameID, gson.toJson(checkmateMsg));
            } else if (currentGame.isInCheck(currentGame.getTeamTurn())) {
                ServerMessage checkMsg =
                        new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                checkMsg.message = affectedPlayer + "is in check";
                connections.broadcastAll(gameID, gson.toJson(checkMsg));
            }

        } catch (Exception e) {
            sendError(session, "error: " + e.getMessage());
        }
    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "unauthorized");
                return;
            }

            String username = auth.username();
            int gameID = command.getGameID();

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session,  "game not found");
                return;
            }

            boolean isWhite = username.equals(gameData.whiteUsername());
            boolean isBlack = username.equals(gameData.blackUsername());

            if (!isWhite && !isBlack) {
                sendError(session, "observer cannot resign");
                return;
            }

            var game = gameData.game();

            if (game.isGameOver()) {
                sendError(session, "game already over");
                return;
            }

            game.setGameOver(true);

            GameData updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );

            gameDAO.updateGame(updatedGame);

            ServerMessage notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            notif.message = username + " resigned the game";

            connections.broadcastAll(gameID, gson.toJson(notif));
        } catch (Exception e) {
            sendError(session, "error: " + e.getMessage());
        }
    }


    //helper functions
    private void sendError(Session session, String message) {
        try {
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            msg.errorMessage = message;

            session.getRemote().sendString(gson.toJson(msg));
        } catch (Exception ignored) {
        }
    }
    private boolean isPlayersTurn(String username, GameData gameData) {
        var game = gameData.game();

        if (game.getTeamTurn() == chess.ChessGame.TeamColor.WHITE) {
            return username.equals(gameData.whiteUsername());
        } else {
            return username.equals(gameData.blackUsername());
        }
    }
}