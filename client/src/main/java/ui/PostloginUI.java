package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import model.*;

import java.util.*;


public class PostloginUI {

    private final ServerFacade server;
    private final Scanner scanner;
    private final String authToken;
    private final Map<Integer, Integer> gameMap = new HashMap<>();

    public PostloginUI(ServerFacade server, Scanner scanner, String authToken) {
        this.server = server;
        this.scanner = scanner;
        this.authToken = authToken;
    }

    public boolean runCommand(String input) {
        String command = input.trim().toLowerCase();

        try {
            switch (command) {
                case "help" -> printHelp();
                case "logout" -> {
                    server.logout(authToken);
                    System.out.println("Logged out.");
                    return true;
                }
                case "create" -> createGame();
                case "list" -> listGames();
                case "join" -> joinGame();
                case "observe" -> observeGame();

                default -> System.out.println("Unknown command. Type 'help' for options.");
            }
        } catch (Exception except){
            handleError(except);
        }

        return false;
    }

    private void printHelp() {
        System.out.println("""
                create - a game
                list - games
                join - a game
                observe - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """);
    }

    private void createGame() throws Exception {
        System.out.print("Game name: ");
        String gamename = scanner.nextLine();

        server.createGame(authToken, gamename);
        System.out.println("Game creation complete");
    }

    private void listGames() throws Exception {
        var result = server.listGames(authToken);

        gameMap.clear();

        int index = 1;
        for (var game : result.games()) {
            gameMap.put(index, game.gameID());

            System.out.println(index + ". " + game.gameName() +
                    " | White: " + game.whiteUsername() +
                    " | Black: " + game.blackUsername());
            index++;
        }

        if (gameMap.isEmpty()) {
            System.out.println("No games :(");
        }
    }

    private void joinGame() throws Exception {
        System.out.print("Game number: ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (!gameMap.containsKey(choice)) {
            System.out.println("Invalid game number.");
            return;
        }

        System.out.print("Color: (WHITE/BLACK): ");
        String color = scanner.nextLine().toUpperCase();

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Invalid color. Enter WHITE or BLACK.");
            return;
        }

        int gameID = gameMap.get(choice);

        server.joinGame(authToken, color, gameID);

        System.out.println("Joined game as " + color);
        ChessGame game = new ChessGame();
        drawBoard(game.getBoard(), color);
    }

    private void observeGame() throws Exception {
        System.out.print("Game number: ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (!gameMap.containsKey(choice)) {
            System.out.println("Invalid game number.");
            return;
        }

        int gameID = gameMap.get(choice);

        server.joinGame(authToken, null, gameID);

        System.out.println("Observing game.");
        ChessGame game = new ChessGame();
        drawBoard(game.getBoard(), "WHITE");
    }

    private void drawBoard(ChessBoard board, String perspective) {
        boolean isWhite = perspective == null || perspective.equalsIgnoreCase("WHITE");

        int rowStart = isWhite ? 8 : 1;
        int rowEnd = isWhite ? 0 : 9;
        int rowStep = isWhite ? -1 : 1;
        int colStart = isWhite ? 1 : 8;
        int colEnd = isWhite ? 9 : 0;
        int colStep = isWhite ? 1 : -1;

        for (int row = rowStart; row != rowEnd; row+= rowStep) {
            System.out.print(row + " ");

            for (int col = colStart; col != colEnd; col += colStep) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                System.out.print(getPieceSymbol(piece) + " ");
            }

            System.out.println();
        }

        System.out.print("  ");
        for (int col = colStart; col != colEnd; col += colStep) {
            char letter = (char) ('a' + col - 1);
            System.out.print(letter + " ");
        }
        System.out.println();
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return ".";
        }

        switch (piece.getPieceType()) {
            case KING: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "K" : "k";
            case QUEEN: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "Q" : "q";
            case ROOK: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "R" : "r";
            case BISHOP: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "B" : "b";
            case KNIGHT: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "N" : "n";
            case PAWN: return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? "P" : "p";
        }
        return "?";
    }

    private void handleError(Exception e) {
        String msg = e.getMessage();

        if (msg == null) {
            System.out.println("Something went wrong");
            return;
        }

        if (msg.contains("403")) {
            System.out.println("This spot is already taken");
        } else if (msg.contains("404")) {
            System.out.println("Game not found");
        } else if (msg.contains("400")) {
            System.out.println("Invalid input. Try again");
        } else {
            System.out.println("Error: " + msg);
        }
    }

}
