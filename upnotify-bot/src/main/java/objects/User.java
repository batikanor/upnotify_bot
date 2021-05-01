package objects;

public class User {
    public int telegramId;
    public int checkLevel;
    public String userName;

    public User(){

    }

    public User(int telegramId,int checkLevel,String userName){
        this.telegramId = telegramId;
        this.checkLevel = checkLevel;
        this.userName = userName;
    }
}
