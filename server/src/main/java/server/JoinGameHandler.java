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

            JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);
            request = new JoinGameRequest(
                    authToken,
                    request.playerColor(),
                    request.gameID()
            );

            joinGameService.joinGame(request);

            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            ctx.status(400);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
}