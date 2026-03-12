package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.CreateGameService;
import model.CreateGameRequest;
import dataaccess.DataAccessException;

public class CreateGameHandler {

    private final CreateGameService createGameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            if (authToken == null) {
                ctx.status(401);
                ctx.result(gson.toJson(new ErrorResponse("Error: unauthorized")));
                return;
            }

            CreateGameRequest body = gson.fromJson(ctx.body(), CreateGameRequest.class);

            if (body == null || body.gameName() == null) {
                ctx.status(400);
                ctx.result(gson.toJson(new ErrorResponse("Error: bad request")));
                return;
            }

            CreateGameRequest request = new CreateGameRequest(authToken, body.gameName());

            CreateGameResult result = createGameService.createGame(request);

            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException e) {

            String msg = e.getMessage();

            if (msg == null) {
                msg = "Error: internal server error";
            }
            if (!msg.toLowerCase().contains("error")) {
                msg = "Error: " + msg;
            }

            if (msg.contains("unauthorized")) {
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