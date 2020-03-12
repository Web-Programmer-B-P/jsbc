package com.data.utils;

import com.data.model.Users;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final public class DaoUtils {
    private static final String REGEX_SEPARATE_POINT = "\\.";

    private DaoUtils() {

    }

    public static String sqlBuilder(String className, Field[] fields) {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT ");
        String fieldPrimaryKey = "";
        int lengthOfArrayFields = fields.length;
        for (int index = 0; index < lengthOfArrayFields; index++) {
            if (index < lengthOfArrayFields - 1) {
                sqlQuery.append(fields[index].getName()).append(", ");
            } else {
                sqlQuery.append(fields[index].getName());
            }
            if (fields[index].getName().contains("id")) {
                fieldPrimaryKey = fields[index].getName();
            }
        }
        String[] tableName = className.split(REGEX_SEPARATE_POINT);
        sqlQuery.append(" FROM ")
                .append(tableName[tableName.length - 1].toLowerCase())
                .append(" WHERE ")
                .append(fieldPrimaryKey)
                .append("=?");
        return sqlQuery.toString();
    }

    public static void setDataToInstance(Object instance, Field[] fields, ResultSet res) throws IllegalAccessException, SQLException {
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().toString().toLowerCase().contains("int")) {
                field.set(instance, res.getInt(field.getName()));
            }
            if (field.getType().toString().contains("String")) {
                field.set(instance, res.getString(field.getName()));
            }
        }
    }

    public static void setCommonFields(PreparedStatement preparedStatement, Users usersForUpdate) throws SQLException {
        preparedStatement.setString(1, usersForUpdate.getName());
        preparedStatement.setString(2, usersForUpdate.getPassword());
        preparedStatement.setString(3, usersForUpdate.getLogin());
    }

    public static Users getReadyUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String login = resultSet.getString("login");
        String password = resultSet.getString("password");
        return new Users(id, name, login, password);
    }
}
