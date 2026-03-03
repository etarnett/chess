package server;

import io.javalin.http.Context;
import service.ClearService;
import dataaccess.*;

public class ClearHandler {
    private final ClearService clearService;

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
            context.json(new ErrorResponse("Error: " + error.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}

}
