package service;

import dataaccess.*;
import model.AuthData;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }
    //logout
    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        AuthData auth = authDAO.getAuth(request.authToken());

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        authDAO.deleteAuth(request.authToken());

        return new LogoutResult();
    }
}
