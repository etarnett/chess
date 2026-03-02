package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private LoginService loginService;

    @BeforeEach
    public void setUp() throws Exception {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        loginService = new LoginService(userDAO, authDAO);

        // IMPORTANT: insert a user before testing login
        userDAO.insertUser(new UserData("user", "pass", "email@test.com"));
    }

    // Positive
    @Test
    public void loginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("user", "pass");

        LoginResult result = loginService.login(request);

        // Verify result
        assertNotNull(result);
        assertEquals("user", result.username());
        assertNotNull(result.authToken());

        // Verify auth token stored in DAO
        AuthData auth = authDAO.getAuth(result.authToken());
        assertNotNull(auth);
        assertEquals("user", auth.username());
    }

    //wrong password
    @Test
    public void loginWrongPassword() {
        LoginRequest request = new LoginRequest("user", "wrongpass");

        assertThrows(DataAccessException.class, () -> {
            loginService.login(request);
        });
    }
}