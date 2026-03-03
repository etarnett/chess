package server;

import io.javalin.http.Context;
import model.ListGameRequest;
import service.ListGameService;
import model.ListGameResult;
import dataaccess.DataAccessException;

public class ListGameHandler {

    private final ListGameService listGameService;

    public ListGameHandler(ListGameService listGameService) {
        this.listGameService = listGameService;
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            if (authToken == null) {
                ctx.status(401);
                ctx.json(new ErrorResponse("Error: unauthorized"));
                return;
            }

            ListGameRequest request = new ListGameRequest(authToken);
            ListGameResult result = listGameService.listGames(request);

            ctx.status(200);
            ctx.json(result);

        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
}