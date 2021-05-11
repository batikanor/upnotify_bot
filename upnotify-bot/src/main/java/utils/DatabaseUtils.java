package utils;


import objects.Request;
import objects.Snapshot;
import objects.User;

import javax.xml.crypto.Data;
import java.io.InputStream;

import java.sql.*;
import java.util.ArrayList;

interface DatabaseUtilsInterface {
	/**
	 * This function is going to insert a user if said user is not present yet
	 * 
	 * This function will be called by UpdateReceiver whenever an update is received, so it must be somewhat efficient
	 * 
	 * if user not exists, creates it as well.
	 * 
	 * Level of user is default level on creation
	 * 
	 * @return reference to User instance
	 */
	public User retrieveUserFromId(long userId, String userName);
	
	
	/**
	 * This function returns the list of all requests from our database.
	 * 
	 * Function will be called from Main.java when the code is run for the first time, so that the requests that are already present will be submitted to the UpnotifyReceiver. 
	 * 
	 * 
	 * 
	 * @return reference to list of Request instances
	 */
	public ArrayList<Request> getRequests();	
	
	/**
	 * 
	 * @param snapshotId
	 * @return snapshot with resp. Id
	 */
	public Snapshot retrieveSnapshotFromId(int snapshotId);
	
	
}

/**
 * @todo Implement DatabaseUtilsInterface
 */
public class DatabaseUtils
{
    public Connection connection = null;
    public String url = "jdbc:sqlite:src/main/resources/upnotify.db";

    private static DatabaseUtils single_instance = null;

    public static DatabaseUtils getDatabaseUtils() {
        if (single_instance == null) {
            single_instance = new DatabaseUtils();
            System.out.println("Instance of 'DatabaseUtils' has been created");
        }
        
        return single_instance;
    
    }

    private DatabaseUtils(){
    }

    public void buildConnection(){
        try {
            connection = DriverManager.getConnection(url);
        }
        catch(SQLException e){
            System.out.println("there is a problem with db connection");
            System.err.println(e.getMessage());
        }
    }

    public void closeConnection(){
        try
        {
            if(connection != null)
                connection.close();
        }
        catch(SQLException e)
        {
            // connection close failed.
            System.err.println(e.getMessage());
        }
    }



    //tablo dbde var ise true yok ise false döndüren bir fonksiyon
    public boolean tableExists(String tableName,Connection conn){
        try{
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, tableName, null);
            if(rs.next()){
                //table exists
                return true;
            }
            else{
                return false;
            }
        }catch(SQLException e){
            System.err.println(e.getMessage());
            return true;
        }
    }

    public void createTables(){
            buildConnection();
            try{
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                
                
                // yok ise USERS tablosunu oluştur
                if(!tableExists("USER",connection)){
                    String create_user_table = "create table USER\n" +
                            "(\n" +
                            "\ttelegramId INTEGER\n" +
                            "\t\tconstraint USER_pk\n" +
                            "\t\t\tprimary key autoincrement,\n" +
                            "\tcheckLevel int default 3,\n" +
                            "\tuserName String\n" +
                            ");\n" +
                            "\n" +
                            "create unique index USER_telegramId_uindex\n" +
                            "\ton USER (telegramId);\n" +
                            "\n" ;

                    try {
                        statement.executeQuery(create_user_table);
                        System.out.print("USER table has created");
                    } catch (SQLException e){
                        System.err.println(e.getMessage());
                    }
                }
                else{

                    System.out.println("USER table already exists");
                    //USERS table allready exists

                }
                // yok ise WEB_PAGES tablosunu oluştur
                if(!tableExists("SNAPSHOT",connection)){
                    String create_webpages_table = "create table SNAPSHOT\n" +
                            "(\n" +
                            "\tsnapshotId INTEGER not null\n" +
                            "\t\tconstraint SNAPSHOT_pk\n" +
                            "\t\t\tprimary key autoincrement,\n" +
                            "\turl String,\n" +
                            "\tscreenshot BLOB,\n" +
                            "\tsiteContentHash String\n" +
                            ");\n" +
                            "\n" +
                            "create unique index SNAPSHOT_snapshotId_uindex\n" +
                            "\ton SNAPSHOT (snapshotId);\n";

                    try {
                        statement.executeQuery(create_webpages_table);
                        System.out.print("SNAPSHOT table has created");
                    } catch (SQLException e){
                        System.err.println(e.getMessage());
                    }
                }else{

                    System.out.println("SNAPSHOT table already exists");
                    //WEB_PAGES table allready exists

                }

                // yok ise WEB_PAGES tablosunu oluştur
                if(!tableExists("REQUEST",connection)){
                    String create_requests_table = "create table REQUEST\n" +
                            "(\n" +
                            "    requestId     INTEGER\n" +
                            "        constraint REQUEST_pk\n" +
                            "            primary key autoincrement,\n" +
                            "    telegramId    int\n" +
                            "        references USER,\n" +
                            "    snapshotId    int,\n" +
                            "        references SNAPSHOT\n" +
                            "    checkInterval int,\n" +
                            "    lastCheckUnix int\n" +
                            ");\n" +
                            "\n" +
                            "create unique index REQUEST_requestId_uindex\n" +
                            "    on REQUEST (requestId);\n" +
                            "\n";


                    try {
                        statement.executeQuery(create_requests_table);
                        System.out.print("REQUEST table has created");
                    } catch (SQLException e){
                        System.err.println(e.getMessage());
                    }
                }else{
                    System.out.println("REQUEST table already exists");
                    //WEB_PAGES table allready exists
                }

            }catch (SQLException e){
                System.err.println(e.getMessage());
            }


            closeConnection();

        }

        //Select all users from USER table and return a user list
        public ArrayList<User> selectUsers(){

            ArrayList<User> userList = new ArrayList<User>();
            buildConnection();
            try{
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                String selectQuery = "SELECT * FROM USER";
                ResultSet rs = statement.executeQuery(selectQuery);
                while (rs.next()) {
                    User selectedUser = new User(rs.getInt("telegramId"),
                            rs.getInt("checkLevel"), rs.getString("userName"));
                    userList.add(selectedUser);
                }
            }catch(SQLException e){
                System.err.println(e.getMessage());
            }
            closeConnection();
            return userList;

        }

        //select a user with a specific telegramId
        public User retrieveUserFromId(int telegramId){
            User selectedUser = new User();
            buildConnection();
            try{
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                String selectFromIdQuery = String.format("SELECT * FROM USER\n" +
                        "WHERE USER.telegramId = %d;",telegramId);
                ResultSet rs = statement.executeQuery(selectFromIdQuery);

                selectedUser.telegramId = rs.getInt("telegramId");
                selectedUser.userName = rs.getString("userName");
                selectedUser.checkLevel = rs.getInt("checkLevel");

            }catch(SQLException e){
                System.err.println(e.getMessage());
            }
            closeConnection();
            return selectedUser;

        }

        // insert a user into USER table
        public void insertUser(int telegramId,int checkLevel, String userName){
            buildConnection();
            try{
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                String insertQuery = String.format("INSERT INTO USER(" +
                        "telegramId,checkLevel,userName)\n"+
                        "VALUES(%d,%d,'%s');",telegramId,checkLevel,userName);

                statement.executeQuery(insertQuery);

            }catch(SQLException e){
                System.err.println(e.getMessage());
            }
            closeConnection();

        }

        public void insertSnapshot(String url, InputStream screenshot, String siteContentHash){
            buildConnection();
            try{
                String insertSnapshotQ= "INSERT INTO SNAPSHOT(url,screenshot,siteContentHash)" + "VALUES(?,?,?)";

                PreparedStatement ps = connection.prepareStatement(insertSnapshotQ);
                ps.setString(1,url);
                ps.setBinaryStream(2,screenshot);
                ps.setString(3,siteContentHash);
                ps.execute();

            }catch(SQLException e){
                System.err.println(e.getMessage());
            }
            closeConnection();
        }


        public InputStream retrieveImageInputStreamFromSnapshotId(int SnapshotId){
            buildConnection();
            InputStream is = null;
            try{
                Statement statement = connection.createStatement();
                String retrieveSnapshot = String.format("SELECT screenshot FROM SNAPSHOT" +
                        "WHERE SnapshotId = %d",SnapshotId);
                ResultSet rs = statement.executeQuery(retrieveSnapshot);
                Blob ablob = rs.getBlob("screenshot");
                is = ablob.getBinaryStream();

            }catch(SQLException e){
                System.err.println(e.getMessage());
            }
        closeConnection();
        return is;
        }


        public void insertRequest(int telegramId,String userName, int checkInterval,String url,InputStream screenshot,
                                  String siteContentHash){

            buildConnection();
            try{
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.


                // insert user if not exists
                User checkUser = retrieveUserFromId(telegramId);
                if(checkUser.userName == null){
                    insertUser(telegramId,checkInterval,userName);
                }


                // insert snapshot and get the id
                insertSnapshot(url,screenshot,siteContentHash);
                int snapshotId = statement.executeQuery("SELECT last_insert_rowid()").getInt(0);

                // insert Request
                int lastCheckedUnix = 100;
                String insertReqQuery = String.format("INSERT INTO REQUEST" +
                        "(telegramId,snapshotId,checkInterval,lastCheckedUnix) VALUES" +
                        "(%d,%d,%d,%d)",telegramId,snapshotId,checkInterval,lastCheckedUnix);
                statement.executeQuery(insertReqQuery);


            }catch(SQLException e){
                System.err.println(e.getMessage());
            }

        }











}
