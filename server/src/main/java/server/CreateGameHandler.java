package server;

import io.javalin.http.Context;
import service.CreateGameService;
import model.CreateGameRequest;
import model.CreateGameResult;
import dataaccess.DataAccessException;

public class CreateGameHandler {

    private final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
            request = new CreateGameRequest(authToken, request.gameName());

            CreateGameResult result = createGameService.createGame(request);

            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
}