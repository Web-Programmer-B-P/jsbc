package com.data.db.interfaces;

import com.data.model.User;
import java.util.List;

public interface UserDao {
    List<User> findAll();
    User findUserById(int user_id);
    int deleteUserById(int user_id);
    int addUser(User user);
    void updateUser(User userForUpdate);
}
