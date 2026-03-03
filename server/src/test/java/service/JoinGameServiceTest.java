package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTest {

    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private JoinGameService service;

    @BeforeEach
    public void setUp() throws Exception {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        service = new JoinGameService(authDAO, gameDAO);

        authDAO.createAuth(new AuthData("token", "user"));

        gameDAO.createGame(new GameData(0, null, null, "game", null));
    }

    //positive testing
    @Test
    public void joinGameSuccess() throws Exception {
        int gameID = 1;

        JoinGameRequest request = new JoinGameRequest("token", "WHITE", gameID);

        model.JoinGameResult result = service.joinGame(request);

        assertNotNull(result);

        GameData game = gameDAO.getGame(gameID);
        assertEquals("user", game.whiteUsername());
    }

    //negative testing (Already taken)
    @Test
    public void joinGameSpotTaken() throws Exception {
        int gameID = 1;

        gameDAO.updateGame(new GameData(gameID, "otherUser", null, "game", null));

        JoinGameRequest request = new JoinGameRequest("token", "WHITE", gameID);

        assertThrows(DataAccessException.class, () -> {
            service.joinGame(request);
        });
    }
}