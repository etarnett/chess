package ui;

public class ErrorHelper {

    public static void handleError(Exception e) {
        String msg = e.getMessage();

        if (msg == null) {
            System.out.println("Something went wrong");
            return;
        }

        if (msg.contains("400")) {
            System.out.println("Invalid input. Try again");
        } else if (msg.contains("401")) {
            System.out.println("Invalid username and password.");
        } else if (msg.contains("403")) {
            System.out.println("This spot is already taken");
        } else if (msg.contains("404")) {
            System.out.println("Game not found");
        } else {
            System.out.println("Error: " + msg);
        }
    }
}