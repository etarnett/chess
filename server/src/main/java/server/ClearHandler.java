package server;

import io.javalin.http.Context;
import service.ClearService;
import com.google.gson.Gson;
import dataaccess.*;

public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context context) {
        try {
            clearService.clear();
            context.status(200);
            context.result("{}");
        } catch (DataAccessException error) {
            context.status(500);
            context.result(gson.toJson(new ErrorResponse("Error: " +error.getMessage())));
        }
    }

    private record ErrorResponse(String message) {}

}
