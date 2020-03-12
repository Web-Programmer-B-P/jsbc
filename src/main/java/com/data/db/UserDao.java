package com.data.db;

import com.data.model.Users;
import com.data.utils.DaoUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private final static UserDao LOGIC_USER_STORAGE = new UserDao();
    private static final String TABLE_NAME = "users";
    private static final String UPDATE_USER_BY_ID = "UPDATE " + TABLE_NAME + " SET name=?, password=?, login=? WHERE id=?";
    private static final String ADD_USER = "INSERT INTO " + TABLE_NAME + " (name, password, login) VALUES (?, ?, ?)";
    private static final String DELETE_BY_ID = "DELETE FROM " + TABLE_NAME + " WHERE id=?";
    private static final String FIND_ALL = "SELECT * FROM " + TABLE_NAME;
    private static final String FIND_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id=?";
    private static final String GET_BACK_NEW_ID = "id";
    private CommonPoolDbConnection poolDbConnection;
    private final static Logger LOG = LogManager.getLogger(UserDao.class.getName());

    private UserDao() {
        this.poolDbConnection = CommonPoolDbConnection.getInstance();
    }

    public static UserDao getInstance() {
        return LOGIC_USER_STORAGE;
    }

    public List<Users> findAll() {
        List<Users> allUsers = new ArrayList<>();
        try (Connection connection = poolDbConnection.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(FIND_ALL);
            while (resultSet.next()) {
                allUsers.add(DaoUtils.getReadyUser(resultSet));
            }
        } catch (SQLException sqle) {
            LOG.error("Ошибка при поиске всех пользователей", sqle);
        }
        return allUsers;
    }

    public Users findUserById(int user_id) {
        Users foundUsers = null;
        try (Connection connection = poolDbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setInt(1, user_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                foundUsers = DaoUtils.getReadyUser(resultSet);
            }
        } catch (SQLException sqle) {
            LOG.error("Ошибка при поиске пользователя по id " + user_id, sqle);
        }
        return foundUsers;
    }

    public int deleteUserById(int user_id) {
        int countDeletedRows = -1;
        try (Connection connection = poolDbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID)) {
            preparedStatement.setInt(1, user_id);
            countDeletedRows = preparedStatement.executeUpdate();
        } catch (SQLException sqle) {
            LOG.error("Ошибка при удалении пользователя по id " + user_id, sqle);
        }
        return countDeletedRows;
    }

    public int addUser(Users users) {
        int result = -1;
        try (Connection connection = poolDbConnection.getConnection();
             PreparedStatement parameters = connection.prepareStatement(ADD_USER, Statement.RETURN_GENERATED_KEYS)) {
            DaoUtils.setCommonFields(parameters, users);
            parameters.execute();
            try (ResultSet rs = parameters.getGeneratedKeys()) {
                if (rs.next()) {
                    result = rs.getInt(GET_BACK_NEW_ID);
                }
            }
        } catch (SQLException sqle) {
            LOG.error("Ошибка при попытке добавить нового пользователя " + users, sqle);
        }
        return result;
    }

    public void updateUser(Users usersForUpdate) {
        try (Connection connection = poolDbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_BY_ID)) {
            DaoUtils.setCommonFields(preparedStatement, usersForUpdate);
            preparedStatement.setInt(4, usersForUpdate.getId());
            preparedStatement.execute();
        } catch (SQLException sqle) {
            LOG.error("Ошибка при попытке обновить пользователя " + usersForUpdate, sqle);
        }
    }

    public Object getById(int user_id, Class clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object instance = clazz.getConstructor().newInstance();
        String className = clazz.getName();
        Field[] fields = instance.getClass().getDeclaredFields();
        try (PreparedStatement prepare = poolDbConnection.getConnection().prepareStatement(DaoUtils.sqlBuilder(className, fields))) {
            prepare.setInt(1, user_id);
            try (ResultSet res = prepare.executeQuery()) {
                if (res.next()) {
                    DaoUtils.setDataToInstance(instance, fields, res);
                }
            }
        } catch (SQLException e) {
            LOG.error("Ошибка при попытке получить пользователя по имени класса ", e);
        }
        return instance;
    }
}
