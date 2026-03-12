package server;

import io.javalin.http.Context;
import service.LogoutService;
import model.LogoutRequest;
import dataaccess.DataAccessException;
import com.google.gson.Gson;

public class LogoutHandler {

    private final LogoutService logoutService;
    private final Gson gson = new Gson();

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            if (authToken == null) {
                ctx.status(401);
                ctx.result(gson.toJson(new ErrorResponse("Error: unauthorized")));
                return;
            }

            LogoutRequest request = new LogoutRequest(authToken);
            logoutService.logout(request);

            ctx.status(200);
            ctx.result("{}");
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