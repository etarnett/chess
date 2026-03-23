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

        var authData = facade.login("player1", "password");
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

}
