package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() {
    }

    @Override
    public void clear() throws DataAccessException {
        var sql = "DELETE FROM auth";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to clear auth tables", ex);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, auth.authToken());
            ps.setString(2, auth.username());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create auth", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var sql = "SELECT authToken, username FROM auth WHERE authToken=?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                //check if line exists
                if (rs.next()) {
                    String token = rs.getString("authToken");
                    String username = rs.getString("username");

                    return new AuthData(token, username);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to get auth", ex);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var sql = "DELETE FROM auth WHERE authToken=?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to delete auth", ex);
        }
    }
}