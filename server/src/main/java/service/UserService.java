package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    //register
    public AuthData register(UserData user) throws DataAccessException {
        if (userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("Error: already exists");
        }

        userDAO.insertUser(user);

        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(authToken, user.username());
        authDAO.createAuth(authData);

        return authData;
    }

    //Login
    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = userDAO.getUser(username);

        //check password
        if (user == null || !user.password().equals(password)) {
            throw new DataAccessException("Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuth(authData);

        return authData;
    }

    //logout
    public void logout(String authToken) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }
}
