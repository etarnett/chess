package websocket;

import chess.ChessGame;
import model.GameData;
import ui.BoardUI;
import websocket.messages.ServerMessage;

public class ClientMessageHandler implements ServerMessageHandler {
    private String perspective = "WHITE";
    private final String username;
    private ChessGame currentGame;

    public ClientMessageHandler(String username) {
        this.username = username;
    }

    @Override
    public void handle(ServerMessage message) {

        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                currentGame = (ChessGame) message.game;

                setPerspective(message.game);

                System.out.println("\n --- Game Update ---");
                BoardUI.drawBoard(currentGame.getBoard(), perspective);
            }

            case NOTIFICATION -> {
                System.out.println("\n[NOTIFICATION] " + message.message);
            }

            case ERROR -> {
                System.out.println("\n[ERROR] " + message.errorMessage);
            }
        }
    }

    public ChessGame getGame() {
        return currentGame;
    }

    private void setPerspective(Object gameObj) {
        try {
            GameData gameData = (GameData) gameObj;

            if (username.equals(gameData.whiteUsername())) {
                perspective = "WHITE";
            } else if (username.equals(gameData.blackUsername())) {
                perspective = "BLACK";
            } else {
                perspective = "WHITE"; // observer default
            }

        } catch (Exception e) {
            perspective = "WHITE"; // fallback safety
        }
    }
}
