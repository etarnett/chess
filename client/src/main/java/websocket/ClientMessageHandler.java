package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import ui.BoardUI;
import websocket.messages.ServerMessage;

public class ClientMessageHandler implements ServerMessageHandler {
    private String perspective = "WHITE";
    private final String username;
    private ChessGame currentGame;

    public ClientMessageHandler(String username, String color) {

        this.username = username;
        this.perspective = color;
    }

    @Override
    public void handle(ServerMessage message) {
        System.out.println("RECEIVED: " + message.getServerMessageType());
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                currentGame = new Gson().fromJson(
                        new Gson().toJson(message.game),
                        ChessGame.class
                );

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

}
