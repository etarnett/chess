package server;

import io.javalin.http.Context;
import service.LogoutService;
import model.LogoutRequest;
import dataaccess.DataAccessException;

public class LogoutHandler {

    private final LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            LogoutRequest request = new LogoutRequest(authToken);
            logoutService.logout(request);

            ctx.status(200);
            ctx.result("{}");
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