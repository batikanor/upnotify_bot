package objects;

public class User {
    public Long telegramId;
    public int checkLevel;
    public String userName;

    public User(){

    }

    public User(Long telegramId,int checkLevel,String userName){
        this.telegramId = telegramId;
        this.checkLevel = checkLevel;
        this.userName = userName;
    }
}
