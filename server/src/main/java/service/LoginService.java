package service;

import dataaccess.*;
import model.AuthData;
import model.LoginRequest;
import model.UserData;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }


    public model.LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = userDAO.getUser(request.username());

        //check password
        if (user == null || !user.password().equals(request.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();

        AuthData authData = new AuthData(authToken, request.username());
        authDAO.createAuth(authData);

        return new model.LoginResult(request.username(), authToken);
    }
}
