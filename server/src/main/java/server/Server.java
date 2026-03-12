package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import java.util.*;

public class Server {

    private final Javalin javalin;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Server() {

        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (DataAccessException e) {
            throw new RuntimeException("Database initialization failed", e);
        }

        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        // Register your endpoints
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        CreateGameService createGameService = new CreateGameService(authDAO, gameDAO);
        ListGameService listGameService = new ListGameService(authDAO, gameDAO);
        JoinGameService joinGameService = new JoinGameService(authDAO, gameDAO);

        //Handlers
        ClearHandler clearHandler = new ClearHandler(clearService);
        RegisterHandler registerHandler = new RegisterHandler(registerService);
        LoginHandler loginHandler = new LoginHandler(loginService);
        LogoutHandler logoutHandler = new LogoutHandler(logoutService);
        CreateGameHandler createGameHandler = new CreateGameHandler(createGameService);
        ListGameHandler listGameHandler = new ListGameHandler(listGameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(joinGameService);


        //Endpoints
        javalin.post("/user", registerHandler::register);
        javalin.delete("/db", clearHandler::clear);
        javalin.post("/session", loginHandler::login);
        javalin.delete("/session", logoutHandler::logout);
        javalin.post("/game", createGameHandler::createGame);
        javalin.get("/game", listGameHandler::listGames);
        javalin.put("/game", joinGameHandler::joinGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}