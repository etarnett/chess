package client;

import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    //REGISTER TESTS
    @Test
    void registerPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertNotNull(authData);
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerFailsDuplicateUser() throws Exception {
        facade.register("player1", "password", "p1@email.com");

        Assertions.assertThrows(Exception.class, () -> {
            facade.register("player1", "password", "p1@email.com");
        });
    }

    //LOGIN TESTS
    @Test
    void loginPositive() throws Exception {
        facade.register("player2", "password", "p2@email.com");

        var authData = facade.login("player2", "password");
        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.authToken());
    }

    @Test
    void loginFailsWrongPassword() throws Exception {
        facade.register("player3", "password", "p3@email.com");

        Assertions.assertThrows(Exception.class, () -> {
            facade.login("user3", "incorrect");
        });
    }

    //LOGOUT TESTS
    @Test
    void logoutPositive() throws Exception {
        var authData = facade.register("player4", "password", "p4@email.com");
        facade.logout(authData.authToken());
    }

    @Test
    void logoutFailsInvalidToken() throws Exception {
        Assertions.assertThrows(Exception.class, () -> {
            facade.logout("bad-token");
        });
    }

    //CREATE GAME TESTS
    @Test
    void createGameWorks() throws Exception {
        var authData = facade.register("player5", "password", "p5@email.com");

        facade.createGame(authData.authToken(), "MyGame");
    }

    @Test
    void createGameFailsNoAuth() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.createGame(null, "Game");
        });
    }


    //LIST GAMES TESTS
    @Test
    void listGamesWorks() throws Exception {
        var authData = facade.register("player6", "password", "p6@email.com");

        facade.createGame(authData.authToken(), "Game1");

        var games = facade.listGames(authData.authToken());

        Assertions.assertNotNull(games);
        Assertions.assertTrue(games.games().size() > 0);
    }


    @Test
    void listGamesFailsInvalidAuth() {
        Assertions.assertThrows(Exception.class, () -> {
            facade.listGames("bad-token");
        });
    }

    //JOIN GAME TESTS
    @Test
    void joinGameWorks() throws Exception {
        var authData = facade.register("player7", "password", "p7@emil.com");

        facade.createGame(authData.authToken(), "Game1");
        var games = facade.listGames(authData.authToken());

        var gameID = games.games().iterator().next().gameID();

        facade.joinGame(authData.authToken(), "WHITE", gameID);
    }

    @Test
    void joinGameFailsBadGameID() throws Exception {
        var auth = facade.register("player8", "password", "p8@email.com");

        Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(auth.authToken(), "WHITE", 9999);
        });
    }

}
