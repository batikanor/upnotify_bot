package utils;

import objects.User;
import org.junit.Assert;
import org.junit.Test;

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
    
    @Test
    public void retrieveSnapshotFromIdTest() {
    	objects.Snapshot snap;
    	int[] ids = {3, 8, 11};
    	for (int id : ids) {
    		snap = DatabaseUtils.getDatabaseUtils().retrieveSnapshotFromId(id);
    		System.out.println("sch=" + snap.siteContentHash + " ss=" + snap.screenshot.getWidth());
    	}
    	
    }

    @Test
    public void retrieveSnapshotFromIdTest2() {
        objects.Snapshot ss = DatabaseUtils.getDatabaseUtils().retrieveSnapshotFromId(13);
        Assert.assertNotEquals(null, ss.screenshot);
    }

    @Test
    public void addRequest(){
        DatabaseUtils.getDatabaseUtils().addRequest((long) 1000,200,"urll",
                null,"hash100");

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
