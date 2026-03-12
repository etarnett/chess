package dataaccess;

import model.GameData;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() throws DataAccessException {
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }
}