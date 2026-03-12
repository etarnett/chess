package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        if (game == null) {
            throw new DataAccessException("Game cannot be null");
        }

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
        var sql = "SELECT * FROM game WHERE gameID=?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                //check if line exists
                if (rs.next()) {
                    String white = rs.getString("whiteUsername");
                    String black = rs.getString("blackUsername");
                    String name = rs.getString("gameName");

                    String json = rs.getString("game");

                    ChessGame game = gson.fromJson(json, ChessGame.class);

                    return new GameData(gameID, white, black, name, game);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to get game", ex);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var sql = "SELECT * FROM game";

        var games = new ArrayList<GameData>();

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("gameID");

                String white = rs.getString("whiteUsername");
                String black = rs.getString("blackUsername");
                String name = rs.getString("gameName");

                String json = rs.getString("game");

                ChessGame game = gson.fromJson(json, ChessGame.class);

                games.add(new GameData(id, white, black, name, game));

            }
            return games;
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to list games", ex);
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

        String json = gson.toJson(gameData.game());

        var sql = """
            UPDATE game SET whiteUsername=?, blackUsername=?, 
            gameName=?, game=? WHERE gameID=?
            """;

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, gameData.whiteUsername());
            ps.setString(2, gameData.blackUsername());
            ps.setString(3, gameData.gameName());
            ps.setString(4, json);
            ps.setInt(5, gameData.gameID());

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Unable to update game", ex);
        }
    }
}