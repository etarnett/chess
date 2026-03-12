package server;

import io.javalin.http.Context;
import service.RegisterService;
import dataaccess.DataAccessException;
import com.google.gson.Gson;
import model.RegisterRequest;
import model.RegisterResult;

public class RegisterHandler {
    private final RegisterService registerService;
    private final Gson gson = new Gson();

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);

            if (request.username()==null || request.password() == null || request.email() == null) {
                ctx.status(400);
                String gsonResult = gson.toJson(new ErrorResponse("Error: bad request"));
                ctx.result(gsonResult);
                return;
            }

            RegisterResult result = registerService.register(request);

            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (DataAccessException e) {
            String msg = e.getMessage();

            if (!msg.toLowerCase().contains("error")) {
                msg = "Error: " + msg;
            }

            if (msg.contains("already")) {
                ctx.status(403);
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