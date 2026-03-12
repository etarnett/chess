package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.LoginService;
import model.LoginRequest;
import model.LoginResult;
import dataaccess.DataAccessException;

public class LoginHandler {

    private final LoginService loginService;
    private final Gson gson = new Gson();

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);

            if (request.username()==null || request.password() == null) {
                ctx.status(400);
                ctx.result(gson.toJson(new ErrorResponse("Error: bad request")));
                return;
            }

            LoginResult result = loginService.login(request);

            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException e) {
            String msg = e.getMessage();

            if (!msg.toLowerCase().contains("error")) {
                msg = "Error: " + msg;
            }

            if (msg.toLowerCase().contains("unauthorized")) {
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