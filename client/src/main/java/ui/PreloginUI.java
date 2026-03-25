package ui;

import client.ServerFacade;
import model.*;
import java.util.*;

public class PreloginUI {

    private final ServerFacade server;
    private final Scanner scanner;

    public PreloginUI(ServerFacade server, Scanner scanner) {
        this.server = server;
        this.scanner = scanner;
    }

    public AuthData runCommand(String input) {
        String command = input.trim().toLowerCase();

        try {
            return switch (command) {
                case "help" -> {
                    printHelp();
                    yield null;
                }
                case "login" -> login();
                case "register" -> register();
                case "clear" -> {
                    server.clear();
                    System.out.println("Database cleared.");
                    yield null;
                } default -> {
                    System.out.println("Unknown command. Type 'help' for options.");
                    yield null;
                }
            };
        } catch (Exception except) {
            ErrorHelper.handleError(except);
            return null;
        }
    }

    private void printHelp() {
        System.out.println("""
                register - to create an account
                login - to play chess
                quit - playing chess
                help - with possible commands
                """);
    }

    private AuthData login() throws Exception {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (username.isBlank() || password.isBlank()) {
            System.out.println("Username and password cannot be empty.");
        }

        AuthData auth = server.login(username, password);
        System.out.println("Logged in as " + auth.username());

        return auth;
    }

    private AuthData register() throws Exception {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("email: ");
        String email = scanner.nextLine();

        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            System.out.println("Username, password, or email cannot be empty.");
            return null;
        }

        AuthData auth = server.register(username, password, email);
        System.out.println("Registered and logged in as " + auth.username());

        return auth;
    }

}
