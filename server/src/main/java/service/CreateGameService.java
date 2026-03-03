package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public model.CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        if (authDAO.getAuth(request.authToken()) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        ChessGame chessGame = new ChessGame();

        GameData game = new GameData(0, null, null, request.gameName(), chessGame);
        int gameID = gameDAO.createGame(game);

        return new CreateGameResult(gameID);
    }
}
