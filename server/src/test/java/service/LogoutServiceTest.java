package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {

    private MemoryAuthDAO authDAO;
    private LogoutService logoutService;

    @BeforeEach
    public void setUp() {
        authDAO = new MemoryAuthDAO();
        logoutService = new LogoutService(authDAO);
    }

    //positive
    @Test
    public void logoutSuccess() throws Exception {
        // Create auth token
        AuthData auth = new AuthData("token123", "user");
        authDAO.createAuth(auth);

        // Verify it exists
        assertNotNull(authDAO.getAuth("token123"));

        // Call logout
        LogoutRequest request = new LogoutRequest("token123");
        LogoutResult result = logoutService.logout(request);

        // Verify result
        assertNotNull(result);

        // Verify token is removed
        assertNull(authDAO.getAuth("token123"));
    }

    //negative
    @Test
    public void logoutInvalidToken() {
        LogoutRequest request = new LogoutRequest("badToken");

        assertThrows(DataAccessException.class, () -> {
            logoutService.logout(request);
        });
    }
}