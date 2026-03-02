package service;

import dataaccess.*;
import model.*;

import chess.ChessGame;

import java.util.*;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    //List games
    public Collection<GameData> listGame(String authToken) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        return gameDAO.listGames();
    }

    //Create Game
    public int createGame(String authToken, String gameName) throws DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        ChessGame chessGame = new ChessGame();

        GameData game = new GameData(0, null, null, gameName, chessGame);

        return gameDAO.createGame(game);
    }

    // Join Game
    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String username = auth.username();

        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            game = new GameData(game.gameID(), username, game.whiteUsername(), game.gameName(), game.game());
        } else {
            throw new DataAccessException("Error: bad request");
        }

        gameDAO.updateGame(game);
    }

}
