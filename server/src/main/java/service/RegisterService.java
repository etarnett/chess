package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (userDAO.getUser(request.username()) != null) {
            throw new DataAccessException("Error: already exists");
        }

        UserData user = new UserData(
                request.username(),
                request.password(),
                request.email()
        );

        userDAO.insertUser(user);

        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }
}
