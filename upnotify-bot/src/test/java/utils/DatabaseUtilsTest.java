package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import objects.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import utils.Config;
import utils.DatabaseUtils;

import javax.validation.constraints.Null;
import javax.xml.crypto.Data;

public class DatabaseUtilsTest {

    @Test
    public void createTablesTest(){
        DatabaseUtils.getDatabaseUtils().createTables();
    }
/*
    @Test
    public void selectUsersTest(){
        ArrayList<User> users = DatabaseUtils.getDatabaseUtils().selectUsers();
        for(User u : users){
            System.out.println(String.format("telegramId: %d\ncheckLevel: %d\nuserName:" +
                    "%s",u.telegramId,u.checkLevel,u.userName));
            System.out.println("**************************************");
        }
    } */
/*
    @Test
    public void insertUserTest(){
        int myTelegramId = 4;
        int myCheckLevel = 1;
        String myUserName = "noob";
        DatabaseUtils.getDatabaseUtils().insertUser(myTelegramId,myCheckLevel,myUserName);
        selectUsersTest();
    } */

    @Test
    public void retrieveUserTest(){
        User x= DatabaseUtils.getDatabaseUtils().retrieveUserFromId(7,"john");
        System.out.println(x.telegramId);
        System.out.println(x.checkLevel);
        System.out.println(x.userName);
    }

/*
    @Test
    public void selectUserFromIdTest(){
        int myTelegramId = 33;

        User selectedUser = DatabaseUtils.getDatabaseUtils().selectUserFromId(myTelegramId);
        if(selectedUser.userName == null){
            System.out.println("no user");
        }
        System.out.println("selected Users info:");
        System.out.println(String.format("telegramId: %d\ncheckLevel: %d\nuserName:" +
                "%s",selectedUser.telegramId,selectedUser.checkLevel,selectedUser.userName));
    }
*/

}
