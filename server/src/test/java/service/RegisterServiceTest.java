package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private RegisterService registerService;

    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        registerService = new RegisterService(userDAO, authDAO);
    }

    //Positive
    @Test
    public void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "pass", "email@test.com");

        RegisterResult result = registerService.register(request);

        // Verify correct
        assertNotNull(result);
        assertEquals("user", result.username());
        assertNotNull(result.authToken());

        // Verify user was added to Data base
        UserData user = userDAO.getUser("user");
        assertNotNull(user);
        assertEquals("user", user.username());

        // Verify auth token was created
        AuthData auth = authDAO.getAuth(result.authToken());
        assertNotNull(auth);
        assertEquals("user", auth.username());
    }

    //Negative
    @Test
    public void registerDuplicateUser() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "pass", "email@test.com");

        // First registration works
        registerService.register(request);

        // Second should fail
        assertThrows(DataAccessException.class, () -> {
            registerService.register(request);
        });
    }
}