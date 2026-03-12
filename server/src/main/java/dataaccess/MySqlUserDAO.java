package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {
    public MySqlUserDAO() {

    }

    @Override
    public void clear() throws DataAccessException {
        var sql = "DELETE FROM user";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to clear users", ex);
        }
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        var sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.setString(3, user.email());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to insert user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT username, password, email FROM user WHERE username=?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                //check if there is next
                if (rs.next()) {
                    String user = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");

                    return new UserData(user, password, email);
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to get user", ex);
        }

    }
}
