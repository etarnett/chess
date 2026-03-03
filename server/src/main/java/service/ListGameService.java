package service;

import dataaccess.*;
import model.*;
import java.util.Collection;


public class ListGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public model.ListGameResult listGames(ListGameRequest request) throws DataAccessException {
        if (authDAO.getAuth(request.authToken()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        Collection<GameData> games = gameDAO.listGames();

        return new ListGameResult(games);
    }


}
