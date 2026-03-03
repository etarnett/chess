package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateGameServiceTest {

    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private CreateGameService service;

    @BeforeEach
    public void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        service = new CreateGameService(authDAO, gameDAO);
    }
    //Positive testing
    @Test
    public void createGameSuccess() throws Exception {
        AuthData auth = new AuthData("token", "user");
        authDAO.createAuth(auth);

        CreateGameRequest request = new CreateGameRequest("token", "game");

        CreateGameResult result = service.createGame(request);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);

        GameData game = gameDAO.getGame(result.gameID());
        assertNotNull(game);
        assertEquals("game", game.gameName());
    }

    //negative testing
    @Test
    public void createGameUnauthorized() {
        CreateGameRequest request = new CreateGameRequest("badToken", "game");

        assertThrows(DataAccessException.class, () -> {
            service.createGame(request);
        });
    }
}