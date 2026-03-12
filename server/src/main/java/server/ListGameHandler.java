package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.ListGameRequest;
import service.ListGameService;
import model.ListGameResult;
import dataaccess.DataAccessException;

public class ListGameHandler {

    private final ListGameService listGameService;
    private final Gson gson = new Gson();

    public ListGameHandler(ListGameService listGameService) {
        this.listGameService = listGameService;
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            if (authToken == null) {
                ctx.status(401);
                ctx.result(gson.toJson(new ErrorResponse("Error: unauthorized")));
                return;
            }

            ListGameRequest request = new ListGameRequest(authToken);
            ListGameResult result = listGameService.listGames(request);

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (DataAccessException e) {
            String msg = e.getMessage();

            if (!msg.toLowerCase().contains("error")) {
                msg = "Error: " + msg;
            }

            if (msg.contains("unauthorized")) {
                ctx.status(401);
            } else {
                ctx.status(500);
            }

            ctx.result(gson.toJson(new ErrorResponse(msg)));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(
                    new ErrorResponse("Error: " + e.getMessage())));
        }
    }

    private record ErrorResponse(String message) {}
}