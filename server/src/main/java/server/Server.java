package server;

import dataaccess.*;
import service.*;
import io.javalin.*;

import java.util.*;

public class Server {

    private final Javalin javalin;

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        RegisterService registerService = new RegisterService(userDAO, authDAO);


        //Handlers
        ClearHandler clearHandler = new ClearHandler(clearService);
        RegisterHandler registerHandler = new RegisterHandler(registerService);

        //Endpoints
        javalin.post("/user", registerHandler::register);
        javalin.delete("/db", clearHandler::clear);


        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
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