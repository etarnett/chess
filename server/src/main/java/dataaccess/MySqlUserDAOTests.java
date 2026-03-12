package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTests {

    private MySqlUserDAO dao;

    @BeforeEach
    public void setup() throws Exception {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();
        dao = new MySqlUserDAO();
        dao.clear();
    }

    // clear positive
    @Test
    public void clearPositive() throws Exception {

        UserData user = new UserData("bob", "password", "email@test.com");

        dao.insertUser(user);

        dao.clear();

        UserData result = dao.getUser("bob");

        assertNull(result);
    }


    // insert positive
    @Test
    public void insertUserPositive() throws Exception {

        UserData user = new UserData("bob", "password", "email@test.com");

        dao.insertUser(user);

        UserData result = dao.getUser("bob");

        assertNotNull(result);
        assertEquals("bob", result.username());
    }


    // insert negative
    @Test
    public void insertUserNegative() throws Exception {

        UserData user = new UserData("bob", "password", "email@test.com");

        dao.insertUser(user);

        assertThrows(DataAccessException.class, () -> {
            dao.insertUser(user);
        });
    }


    // get user positive
    @Test
    public void getUserPositive() throws Exception {

        UserData user = new UserData("bob", "password", "email@test.com");

        dao.insertUser(user);

        UserData result = dao.getUser("bob");

        assertNotNull(result);
        assertEquals("bob", result.username());
        assertEquals("email@test.com", result.email());
    }


    // get user negative
    @Test
    public void getUserNegative() throws Exception {

        UserData result = dao.getUser("fakeUser");

        assertNull(result);
    }
}