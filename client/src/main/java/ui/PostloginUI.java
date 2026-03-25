package ui;

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
            System.out.println("Error: " + except.getMessage());
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

        int gameID = gameMap.get(choice);

        server.joinGame(authToken, color, gameID);

        System.out.println("Joined game as " + color);
        System.out.println("Insert chess board drawing later");
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
        System.out.println("Insert chess board drawing later");
    }

}
