package serviceUnitTests;

import dataaccess.*;
import model.*;
import service.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws Exception {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();

        // Add some data before clearing
        userDAO.insertUser(new UserData("user", "pass", "email@test.com"));
        gameDAO.createGame(new GameData(1, null, null, "game", null));
        authDAO.createAuth(new AuthData("token", "user"));
    }

    @Test
    public void clearSuccess() throws Exception {
        // Verify data exists before clear
        assertNotNull(userDAO.getUser("user"));
        assertNotNull(gameDAO.getGame(1));
        assertNotNull(authDAO.getAuth("token"));

        // Call clear service
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        clearService.clear();

        // Verify everything is cleared
        assertNull(userDAO.getUser("user"));
        assertNull(gameDAO.getGame(1));
        assertNull(authDAO.getAuth("token"));
    }
}