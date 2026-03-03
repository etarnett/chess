package server;

import io.javalin.http.Context;
import service.JoinGameService;
import model.JoinGameRequest;
import dataaccess.DataAccessException;

public class JoinGameHandler {

    private final JoinGameService joinGameService;

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            if (authToken == null) {
                ctx.status(401);
                ctx.json(new ErrorResponse("Error: unauthorized"));
                return;
            }

            JoinGameRequest body = ctx.bodyAsClass(JoinGameRequest.class);

            if (body.playerColor() == null) {
                ctx.status(400);
                ctx.json(new ErrorResponse("Error: bad request"));
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
            if (e.getMessage().contains("already")) {
                ctx.status(403);
            } else if (e.getMessage().contains("unauthorized")) {
                ctx.status(401);
            } else {
                ctx.status(400);
            }

            ctx.json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
}