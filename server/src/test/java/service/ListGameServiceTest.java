package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ListGameServiceTest {

    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private ListGameService service;

    @BeforeEach
    public void setUp() throws Exception {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        service = new ListGameService(authDAO, gameDAO);

        authDAO.createAuth(new AuthData("token", "user"));

        gameDAO.createGame(new GameData(0, null, null, "game1", null));
        gameDAO.createGame(new GameData(0, null, null, "game2", null));
    }

    @Test
    public void listGamesSuccess() throws Exception {
        ListGameRequest request = new ListGameRequest("token");

        model.ListGameResult result = service.listGames(request);

        assertNotNull(result);
        Collection<GameSummary> games = result.games();
        assertEquals(2, games.size());
    }

    @Test
    public void listGamesUnauthorized() {
        ListGameRequest request = new ListGameRequest("badToken");

        assertThrows(DataAccessException.class, () -> {
            service.listGames(request);
        });
    }
}