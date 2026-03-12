package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTests {

    private MySqlAuthDAO dao;

    @BeforeEach
    public void setup() throws Exception {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();

        dao = new MySqlAuthDAO();
        dao.clear();
    }

    // clear positive
    @Test
    public void clearPositive() throws Exception {

        AuthData auth = new AuthData("token123", "bob");

        dao.createAuth(auth);

        dao.clear();

        AuthData result = dao.getAuth("token123");

        assertNull(result);
    }

    // create auth positive
    @Test
    public void createAuthPositive() throws Exception {

        AuthData auth = new AuthData("token123", "bob");

        dao.createAuth(auth);

        AuthData result = dao.getAuth("token123");

        assertNotNull(result);
        assertEquals("bob", result.username());
        assertEquals("token123", result.authToken());
    }

    // create auth negative
    @Test
    public void createAuthNegative() throws Exception {

        AuthData auth = new AuthData("token123", "bob");

        dao.createAuth(auth);

        assertThrows(DataAccessException.class, () -> {
            dao.createAuth(auth);
        });
    }

    // get auth positive
    @Test
    public void getAuthPositive() throws Exception {

        AuthData auth = new AuthData("token123", "bob");

        dao.createAuth(auth);

        AuthData result = dao.getAuth("token123");

        assertNotNull(result);
        assertEquals("bob", result.username());
    }

    // get auth negative
    @Test
    public void getAuthNegative() throws Exception {

        AuthData result = dao.getAuth("fakeToken");

        assertNull(result);
    }

    // delete auth positive
    @Test
    public void deleteAuthPositive() throws Exception {

        AuthData auth = new AuthData("token123", "bob");

        dao.createAuth(auth);

        dao.deleteAuth("token123");

        AuthData result = dao.getAuth("token123");

        assertNull(result);
    }

    // delete auth negative
    @Test
    public void deleteAuthNegative() throws Exception {

        assertDoesNotThrow(() -> {
            dao.deleteAuth("fakeToken");
        });
    }
}