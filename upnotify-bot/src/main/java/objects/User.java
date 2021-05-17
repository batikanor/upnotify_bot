package objects;
// For chats, this User is the telegram chat
// add a 4th field for dichotomization between normal user / chat if u want to only allow chat admins to use bot in the future
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
