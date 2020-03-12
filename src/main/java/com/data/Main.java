package com.data;

import com.data.db.UserDao;
import com.data.model.Users;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        int[] usersId = new int[2];
        //Add a new user to db
        UserDao operation = UserDao.getInstance();
        usersId[0] = operation.addUser(new Users("Abdula", "abdulchik", "123"));
        usersId[1] = operation.addUser(new Users("Simpson", "gomer@@@", "22334"));

        //Find User by id
        Users foundUsers = operation.findUserById(usersId[1]);
        System.out.println(foundUsers);

        //Delete User by id and return int count of touch rows
        int countDeletedRows = operation.deleteUserById(usersId[0]);
        System.out.println(countDeletedRows);

        //Find all users
        int countUsers = operation.findAll().size();
        System.out.println(countUsers);

        //Update User
        Users usersForUpdate = operation.findUserById(usersId[1]);
        usersForUpdate.setLogin("newLogin");
        usersForUpdate.setPassword("555");
        operation.updateUser(usersForUpdate);
        usersForUpdate = operation.findUserById(usersId[1]);
        System.out.println(usersForUpdate);

        //Find User by class
        Users user = (Users) operation.getById(1, Users.class);
        System.out.println(user);
    }
}
