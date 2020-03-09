package com.data.db;

import com.data.db.interfaces.UserDao;
import com.data.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogicUserStorage implements UserDao {
    private final static LogicUserStorage LOGIC_USER_STORAGE = new LogicUserStorage();
    private static final String TABLE_NAME = "users";
    private CommonPoolDbConnection poolDbConnection;
    private final static Logger LOG = LogManager.getLogger(LogicUserStorage.class.getName());

    private LogicUserStorage() {
        this.poolDbConnection = CommonPoolDbConnection.getInstance();
    }

    public static LogicUserStorage getInstance() {
        return LOGIC_USER_STORAGE;
    }

    @Override
    public List<User> findAll() {
        List<User> allUsers = new ArrayList<>();
        try (Connection connection = poolDbConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_NAME);
            while (resultSet.next()) {
                allUsers.add(getReadyUser(resultSet));
            }
        } catch (SQLException sqle) {
            LOG.error("Ошибка при поиске всех пользователей", sqle);
        }
        return allUsers;
    }

    @Override
    public User findUserById(int user_id) {
        User foundUser = null;
        try (Connection connection = poolDbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "
                     + TABLE_NAME + " WHERE user_id=?")) {
            preparedStatement.setInt(1, user_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                foundUser = getReadyUser(resultSet);
            }
        } catch (SQLException sqle) {
            LOG.error("Ошибка при поиске пользователя по id " + user_id, sqle);
        }
        return foundUser;
    }

    @Override
    public int deleteUserById(int user_id) {
        int countDeletedRows = -1;
        try (Connection connection = poolDbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE user_id=?")) {
            preparedStatement.setInt(1, user_id);
            countDeletedRows = preparedStatement.executeUpdate();
        } catch (SQLException sqle) {
            LOG.error("Ошибка при удалении пользователя по id " + user_id, sqle);
        }
        return countDeletedRows;
    }

    @Override
    public int addUser(User user) {
        int result = -1;
        try (Connection connection = poolDbConnection.getConnection();
             PreparedStatement parameters = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (name, password, login)" +
                     " VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            setCommonFields(parameters, user);
            parameters.execute();
            try (ResultSet rs = parameters.getGeneratedKeys();) {
                if (rs.next()) {
                    result = rs.getInt(1);
                }
            }
        } catch (SQLException sqle) {
            LOG.error("Ошибка при попытке добавить нового пользователя " + user, sqle);
        }
        return result;
    }

    @Override
    public void updateUser(User userForUpdate) {
        try (Connection connection = poolDbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + TABLE_NAME
                     + " SET name=?, password=?, login=? WHERE user_id=?")) {
            setCommonFields(preparedStatement, userForUpdate);
            preparedStatement.setInt(4, userForUpdate.getId());
            preparedStatement.execute();
        } catch (SQLException sqle) {
            LOG.error("Ошибка при попытке обновить пользователя " + userForUpdate, sqle);
        }
    }

    private void setCommonFields(PreparedStatement preparedStatement, User userForUpdate) throws SQLException {
        preparedStatement.setString(1, userForUpdate.getName());
        preparedStatement.setString(2, userForUpdate.getPassword());
        preparedStatement.setString(3, userForUpdate.getLogin());
    }

    private User getReadyUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String name = resultSet.getString(2);
        String login = resultSet.getString(3);
        String password = resultSet.getString(4);
        return new User(id, name, login, password);
    }
}
