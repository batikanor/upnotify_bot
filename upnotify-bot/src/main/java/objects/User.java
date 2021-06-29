package objects;
// For chats, this User is the telegram chat
// add a 4th field for dichotomization between normal user / chat if u want to only allow chat admins to use bot in the future


/**
 * Every person (or group) that uses the upnotify bot is a User.
 * This class is to represent the User table from the DB
 */
public class User {
    public Long telegramId;
    public int checkLevel;
    public String userName;

    /** 
     * Default empty constructor
     */
    public User(){

    }

    /**
     * Instantiates a User by mapping all fields (from the DB) to fields in instance to-be-created.
     */
    public User(Long telegramId,int checkLevel,String userName){
        this.telegramId = telegramId;
        this.checkLevel = checkLevel;
        this.userName = userName;
    }
}
