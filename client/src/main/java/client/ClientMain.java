package client;

import chess.*;
import ui.PreloginUI;
import model.AuthData;

import java.util.*;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        new ClientMain().run();
    }

    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    private ServerFacade server;
    private PreloginUI preloginUI;
    private AuthData authData = null;

    public void run() {
        server = new ServerFacade(8080);
        preloginUI = new ui.PreloginUI(server, scanner);

        while (running) {
            printPrompt();
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                running = false;
                continue;
            }

            if (authData == null) {
                authData = preloginUI.runCommand(input);
            } else {
                System.out.println("Postlogin UI coming next...");
            }
        }
        System.out.println("Goodbye!");
    }

    private void printPrompt() {
        if (authData == null) {
            System.out.print("[Logged Out] > ");
        } else {
            System.out.print("[Logged In] > ");
        }
    }




}
