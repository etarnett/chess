package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO {
    public MySqlUserDAO() throw DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}
