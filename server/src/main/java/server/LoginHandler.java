package server;

import io.javalin.http.Context;
import service.LoginService;
import model.LoginRequest;
import model.LoginResult;
import dataaccess.DataAccessException;

public class LoginHandler {

    private final LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);

            if (request.username()==null || request.password() == null) {
                ctx.status(400);
                ctx.json(new ErrorResponse("Error: bad request"));
                return;
            }

            LoginResult result = loginService.login(request);

            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
}