package client;

import chess.*;
import java.util.*;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        new ClientMain().run();
    }

    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public void run() {
        while (running) {
            printPrompt();
            String input = scanner.nextLine();
            handleCommand(input);
        }

        System.out.println("Goodbye!");
    }

    private void printPrompt() {
        System.out.print("> ");
    }

    private void handleCommand(String input) {
        String command = input.trim().toLowerCase();

        switch (command) {
            case "help" -> printHelp();
            case "quit" -> running = false;
            default -> System.out.println("Unknown command. Type 'help' for options.");
        }

    }

    private void printHelp() {
        System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """);
    }


}
