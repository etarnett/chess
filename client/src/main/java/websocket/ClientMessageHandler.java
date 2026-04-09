package websocket;

import chess.ChessGame;
import ui.BoardUI;
import websocket.messages.ServerMessage;

public class ClientMessageHandler implements ServerMessageHandler {
    @Override
    public void handle(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                ChessGame game = (ChessGame) message.game;
                System.out.println("\n --- Game Update ---");
                BoardUI.drawBoard(game.getBoard(), "WHITE");
            }

            case NOTIFICATION -> {
                System.out.println("\n[NOTIFICATION] " + message.message);
            }

            case ERROR -> {
                System.out.println("\n[ERROR] " + message.errorMessage);
            }
        }
    }
}
