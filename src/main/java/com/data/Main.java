package com.data;

import com.data.db.LogicUserStorage;
import com.data.db.interfaces.UserDao;
import com.data.model.User;

public class Main {
    public static void main(String[] args) {
        int[] usersId = new int[2];
        //Add a new user to db
        UserDao operation = LogicUserStorage.getInstance();
        usersId[0] = operation.addUser(new User("Abdula", "abdulchik", "123"));
        usersId[1] = operation.addUser(new User("Simpson", "gomer@@@", "22334"));

        //Find User by id
        User foundUser = operation.findUserById(usersId[1]);
        System.out.println(foundUser);

        //Delete User by id and return int count of touch rows
        int countDeletedRows = operation.deleteUserById(usersId[0]);
        System.out.println(countDeletedRows);

        //Find all users
        int countUsers = operation.findAll().size();
        System.out.println(countUsers);

        //Update User
        User userForUpdate = operation.findUserById(usersId[1]);
        userForUpdate.setLogin("newLogin");
        userForUpdate.setPassword("555");
        operation.updateUser(userForUpdate);
        userForUpdate = operation.findUserById(usersId[1]);
        System.out.println(userForUpdate);
    }
}
