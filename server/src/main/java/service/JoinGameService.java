package service;

import dataaccess.*;
import model.*;


public class JoinGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public model.JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        AuthData auth = authDAO.getAuth(request.authToken());

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData game = gameDAO.getGame(request.gameID());

        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String username = auth.username();

        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        }
        else if (request.playerColor().equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), username, game.whiteUsername(), game.gameName(), game.game());
        }
        else {
            throw new DataAccessException("Error: bad request");
        }

        gameDAO.updateGame(game);


        return new JoinGameResult();
    }
}
