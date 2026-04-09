package ui;

import chess.*;
import client.ServerFacade;
import model.*;
import websocket.*;

import java.util.*;


public class PostloginUI {

    private final ServerFacade server;
    private final Scanner scanner;
    private final String authToken;
    private WebSocketFacade ws;
    private String currentColor = "WHITE";
    private int currentGameID;
    private String username;
    private ClientMessageHandler handler;

    private final Map<Integer, Integer> gameMap = new HashMap<>();

    public PostloginUI(ServerFacade server, Scanner scanner, String authToken, String username) {
        this.server = server;
        this.scanner = scanner;
        this.authToken = authToken;
        this.username = username;
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
            ErrorHelper.handleError(except);
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

            String whiteName = game.whiteUsername() == null ? "OPEN" : game.whiteUsername();
            String blackName = game.blackUsername() == null ? "OPEN" : game.blackUsername();


            System.out.println(index + ". " + game.gameName() +
                    " | White: " + whiteName +
                    " | Black: " + blackName);
            index++;
        }

        if (gameMap.isEmpty()) {
            System.out.println("No games :(");
        }
    }

    private void joinGame() throws Exception {
        System.out.print("Game number: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());

        } catch (NumberFormatException e) {
            System.out.println("Must input number");
            return;
        }

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

        currentGameID = gameID;
        currentColor = color;

        handler = new ClientMessageHandler(username);

        ws = new WebSocketFacade("http://localhost:8080", handler);
        ws.connect(authToken, gameID);

        gameLoop();

    }

    private void observeGame() throws Exception {
        System.out.print("Game number: ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (!gameMap.containsKey(choice)) {
            System.out.println("Invalid game number.");
            return;
        }

        int gameID = gameMap.get(choice);



        currentGameID = gameID;
        currentColor = "WHITE";

        handler = new ClientMessageHandler(username);

        ws = new WebSocketFacade("http://localhost:8080", handler);

        ws.connect(authToken, gameID);

        System.out.println("Observing game.");

        gameLoop();
    }

    private void gameLoop() {
        while (true) {
            System.out.print("\n[game] > ");
            String input = scanner.nextLine();

            try {
                if (input.equalsIgnoreCase("leave")) {
                    ws.leave(authToken, currentGameID);
                    ws.close();
                    System.out.println("Left game.");
                    break;
                } else if (input.equalsIgnoreCase("resign")) {
                    ws.resign(authToken, currentGameID);
                    System.out.println("You resigned.");
                } else if (input.startsWith("move")) {
                    handleMove(input);
                } else if (input.equalsIgnoreCase("help")) {
                    printGameHelp();
                } else if (input.startsWith("highlight")) {
                    highlightMoves(input);
                }
                else {
                    System.out.println("Unknown command");
                }
            } catch (Exception e) {
                ErrorHelper.handleError(e);
            }
        }
    }

    private void handleMove(String input) throws Exception {
        // format: move e2 e4
        String[] parts = input.split(" ");
        if (parts.length < 3) {
            System.out.println("Usage: move <from> <to>");
            return;
        }

        ChessPosition from = parsePosition(parts[1]);
        ChessPosition to = parsePosition(parts[2]);

        ChessMove move = new ChessMove(from, to, null);
        ws.makeMove(authToken, currentGameID, move);
    }

    private ChessPosition parsePosition(String pos) {
        int col = pos.charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(pos.charAt(1));
        return new ChessPosition(row, col);
    }

    private void highlightMoves(String input) {
        try {
            String[] parts = input.split(" ");
            if (parts.length < 2) {
                System.out.println("Usage: highlight <position>");
                return;
            }

            ChessPosition position = parsePosition(parts[1]);
            ChessGame game = getCurrentGame();
            if (game == null) {
                System.out.println("Game not loaded yet.");
                return;
            }

            Collection<ChessMove> moves = game.validMoves(position);

            BoardUI.drawBoardWithHighlights(
                    game.getBoard(),
                    currentColor,
                    position,
                    moves
            );

        } catch (Exception e) {
            System.out.println("Invalid position.");
        }
    }

    private ChessGame getCurrentGame() {
        return handler.getGame();
    }

    private void printGameHelp() {
        System.out.println("""
            move <from> <to>  - make a move (e.g. move e2 e4)
            leave             - leave the game
            resign            - resign the game
            help              - show commands
            """);
    }
}
