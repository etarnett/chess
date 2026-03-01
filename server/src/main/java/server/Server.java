package server;

import dataaccess.*;
import service.ClearService;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

        javalin.delete("/db", ctx -> {
            try {
                clearService.clear();
                ctx.status(200);
                ctx.json(Map.of());
            } catch (Exception e) {
                ctx.status(500);
                ctx.json((Map.of("message", "Error: " + e.getMessage())));
            }
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}