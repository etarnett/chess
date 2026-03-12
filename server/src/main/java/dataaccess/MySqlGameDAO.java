package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    public MySqlGameDAO() {
    }

    @Override
    public void clear() throws DataAccessException {
        var sql = "DELETE FROM game";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to clear game table", ex);
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String json = gson.toJson(game.game());

        var sql = """
            INSERT INTO game (whiteUsername, blackUsername, gameName, game)
            VALUES (?, ?, ?, ?)
            """;

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, json);

            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new DataAccessException("Failed to generate game ID");
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create game", ex);
        }
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