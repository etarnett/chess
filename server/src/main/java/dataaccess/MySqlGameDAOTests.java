package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTests {

    private MySqlGameDAO dao;

    @BeforeEach
    public void setup() throws Exception {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();

        dao = new MySqlGameDAO();
        dao.clear();
    }

    // clear positive
    @Test
    public void clearPositive() throws Exception {

        GameData game = new GameData(0, null, null, "testGame", new ChessGame());
        dao.createGame(game);

        dao.clear();

        Collection<GameData> games = dao.listGames();

        assertTrue(games.isEmpty());
    }

    // create game positive
    @Test
    public void createGamePositive() throws Exception {

        GameData game = new GameData(0, null, null, "testGame", new ChessGame());

        int id = dao.createGame(game);

        GameData result = dao.getGame(id);

        assertNotNull(result);
        assertEquals("testGame", result.gameName());
    }

    // create game negative
    @Test
    public void createGameNegative() {

        assertThrows(DataAccessException.class, () -> {
            dao.createGame(null);
        });
    }

    // get game positive
    @Test
    public void getGamePositive() throws Exception {

        GameData game = new GameData(0, null, null, "testGame", new ChessGame());

        int id = dao.createGame(game);

        GameData result = dao.getGame(id);

        assertNotNull(result);
        assertEquals(id, result.gameID());
    }

    // get game negative
    @Test
    public void getGameNegative() throws Exception {

        GameData result = dao.getGame(9999);

        assertNull(result);
    }

    // list game positive
    @Test
    public void listGamesPositive() throws Exception {

        GameData game1 = new GameData(0, null, null, "game1", new ChessGame());
        GameData game2 = new GameData(0, null, null, "game2", new ChessGame());

        dao.createGame(game1);
        dao.createGame(game2);

        Collection<GameData> games = dao.listGames();

        assertEquals(2, games.size());
    }

    // list game negative
    @Test
    public void listGamesNegative() throws Exception {

        Collection<GameData> games = dao.listGames();

        assertTrue(games.isEmpty());
    }

    // update game positive
    @Test
    public void updateGamePositive() throws Exception {

        GameData game = new GameData(0, null, null, "testGame", new ChessGame());

        int id = dao.createGame(game);

        GameData updated = new GameData(id, "white", "black", "updatedGame", new ChessGame());

        dao.updateGame(updated);

        GameData result = dao.getGame(id);

        assertEquals("white", result.whiteUsername());
        assertEquals("black", result.blackUsername());
        assertEquals("updatedGame", result.gameName());
    }

    // update game negative
    @Test
    public void updateGameNegative() {

        GameData game = new GameData(9999, "white", "black", "badGame", new ChessGame());

        assertDoesNotThrow(() -> dao.updateGame(game));
    }
}