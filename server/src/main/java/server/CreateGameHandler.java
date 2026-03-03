package server;

import io.javalin.http.Context;
import service.CreateGameService;
import model.CreateGameRequest;
import dataaccess.DataAccessException;

public class CreateGameHandler {

    private final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            if (authToken == null) {
                ctx.status(401);
                ctx.json(new ErrorResponse("Error: unauthorized"));
                return;
            }

            CreateGameRequest body = ctx.bodyAsClass(CreateGameRequest.class);

            if (body.gameName() == null) {
                ctx.status(400);
                ctx.json(new ErrorResponse("Error: bad request"));
                return;
            }

            model.CreateGameRequest request = new CreateGameRequest(authToken, body.gameName());

            CreateGameResult result = createGameService.createGame(request);

            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
}