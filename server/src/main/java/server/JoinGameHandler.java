package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.JoinGameService;
import model.JoinGameRequest;
import dataaccess.DataAccessException;

public class JoinGameHandler {

    private final JoinGameService joinGameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            if (authToken == null) {
                ctx.status(401);
                ctx.result(gson.toJson(new ErrorResponse("Error: unauthorized")));
                return;
            }

            JoinGameRequest body = gson.fromJson(ctx.body(), JoinGameRequest.class);

            if (body == null || body.playerColor() == null) {
                ctx.status(400);
                ctx.result(gson.toJson(new ErrorResponse("Error: bad request")));
                return;
            }

            JoinGameRequest request = new JoinGameRequest(
                    authToken,
                    body.playerColor(),
                    body.gameID()
            );

            joinGameService.joinGame(request);

            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            String msg = e.getMessage();

            if (msg == null) {
                msg = "Error: internal server error";
            }

            // Ensure message contains "Error"
            if (!msg.toLowerCase().contains("error")) {
                msg = "Error: " + msg;
            }

            if (msg.contains("already")) {
                ctx.status(403);
            } else if (msg.contains("unauthorized")) {
                ctx.status(401);
            } else if (msg.contains("bad request")) {
                ctx.status(400);
            } else {
                ctx.status(500);
            }

            ctx.result(gson.toJson(new ErrorResponse(msg)));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
    }

    private record ErrorResponse(String message) {}
}