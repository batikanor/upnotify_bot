package utils;


import objects.Request;
import objects.Snapshot;
import objects.User;
//import org.openqa.selenium.devtools.database.Database;
import upnotify_bot.UpnotifyBot;

// import javax.imageio.ImageIO;
// import javax.xml.crypto.Data;

// import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;

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
 * @todo Implement DatabaseUtilsInterface'
 * @todo check multithreading capabilities
 */
public class DatabaseUtils implements DatabaseUtilsInterface
{



    public Connection connection = null;
    //public String url = "jdbc:" + Config.getConfig().DATABASE_ENGINE + ":upnotify-bot/src/main/resources/upnotify.db"; // for vscode
    //public String url = "jdbc:" + Config.getConfig().DATABASE_ENGINE + ":src/main/resources/upnotify.db"; // for eclipse
    public String url = "jdbc:" + Config.getConfig().DATABASE_ENGINE + ":" + this.getClass().getResource("/upnotify.db");
    private static DatabaseUtils single_instance = null;

    public static DatabaseUtils getDatabaseUtils() {
        if (single_instance == null) {
            single_instance = new DatabaseUtils();
            System.out.println("Instance of 'DatabaseUtils' has been created");

            //below code creates a new .db file if not exists in resources folder
            if (DatabaseUtils.getDatabaseUtils().url.contentEquals("jdbc:" + Config.getConfig().DATABASE_ENGINE + ":null")) {
                try {
                    DatabaseUtils.getDatabaseUtils().connection = DriverManager.getConnection("jdbc:" + Config.getConfig().DATABASE_ENGINE + ":upnotify-bot/src/main/resources/upnotify.db"); //this can be improved because of different IDE path problems
                    if (DatabaseUtils.getDatabaseUtils().connection != null) {
                        DatabaseMetaData meta = DatabaseUtils.getDatabaseUtils().connection.getMetaData();
                        System.out.println("The driver name is " + meta.getDriverName());
                        System.out.println("A new database has been created.");
                        DatabaseUtils.getDatabaseUtils().closeConnection();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("url: " + DatabaseUtils.getDatabaseUtils().url);
            DatabaseUtils.getDatabaseUtils().createTables();
        }
        
        return single_instance;
    
    }

    private DatabaseUtils(){
        

    }
    
    //Instead of relative path use absolute path to resolve path conflict between different IDEs
    //However if it's ever meant to be run from a standalone jar file this path has to be changed, like the same place as the jar file run from
    private String getDatabasePath() {
    	Path path = Paths.get("");
		String s = path.toAbsolutePath().toString();
		String s0  = s.split("upnotify_bot")[0];
		s0 += "upnotify_bot\\upnotify-bot\\src\\main\\resources\\upnotify.db";
		
		return s0;
    }
    
    public void buildConnection(){
    	System.out.println("Building the db connection");
        try {
            connection = DriverManager.getConnection("jdbc:" + Config.getConfig().DATABASE_ENGINE + ":" + getDatabasePath());
        }
        catch(SQLException e){
            //System.out.println("there is a problem with db connection");
            e.printStackTrace();
        }
        
    }

    public void closeConnection(){
        System.out.println("Closing the db connection");
        try
        {
            if(connection != null)
                connection.close();
        }
        catch(SQLException e)
        {
            // connection close failed.
            e.printStackTrace();
        }
    }



    //tablo dbde var ise true yok ise false döndüren bir fonksiyon
    private boolean tableExists(String tableName,Connection conn){
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
            e.printStackTrace();
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
                    String create_user_table = "create table if not exists USER\n" +
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
                        statement.executeUpdate(create_user_table);
                        System.out.println("USER table has been created");
                    } catch (SQLException e){
                        e.printStackTrace();
                    }
                }
                else{

                    System.out.println("USER table already exists");
                    //USERS table allready exists

                }
                // yok ise WEB_PAGES tablosunu oluştur
                if(!tableExists("SNAPSHOT",connection)){
                    String create_webpages_table = "create table if not exists SNAPSHOT\n" +
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
                        statement.executeUpdate(create_webpages_table);
                        System.out.println("SNAPSHOT table has been created");
                    } catch (SQLException e){
                        e.printStackTrace();
                    }
                }else{

                    System.out.println("SNAPSHOT table already exists");
                    //WEB_PAGES table allready exists

                }

                // yok ise WEB_PAGES tablosunu oluştur
                if(!tableExists("REQUEST",connection)){
                    String create_requests_table = "create table if not exists REQUEST\n" +
                            "(\n" +
                            "    requestId     INTEGER\n" +
                            "        constraint REQUEST_pk\n" +
                            "            primary key autoincrement,\n" +
                            "    telegramId INTEGER,\n" +
                            "    snapshotId INTEGER,\n" +
                            "    checkInterval int,\n" +
                            "    isActive int,\n"+
                            "    lastCheckUnix int,\n" +
                            "    FOREIGN KEY(telegramId) REFERENCES USER(telegramId),\n" +
                            "    FOREIGN KEY(snapshotId) REFERENCES SNAPSHOT(snapshotId)\n" +
                            ");\n" +
                            "\n" +
                            "create unique index REQUEST_requestId_uindex\n" +
                            "    on REQUEST (requestId);\n" +
                            "\n";


                    try {
                        statement.executeUpdate(create_requests_table);
                        System.out.println("REQUEST table has been created");
                    } catch (SQLException e){
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("REQUEST table already exists");
                    //WEB_PAGES table allready exists
                }

            }catch (SQLException e){
                e.printStackTrace();
            }


            closeConnection();

            if (Config.getConfig().DATABASE_ENGINE.toLowerCase().startsWith("sqlite")){
                fixPragmaWAL();
            }
        }

        //Select all users from USER table and return a user list
        private ArrayList<User> selectUsers(){

            ArrayList<User> userList = new ArrayList<User>();
            buildConnection();
            try{
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                String selectQuery = "SELECT * FROM USER";
                ResultSet rs = statement.executeQuery(selectQuery);
                while (rs.next()) {
                    User selectedUser = new User(rs.getLong("telegramId"),
                            rs.getInt("checkLevel"), rs.getString("userName"));
                    userList.add(selectedUser);
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
            closeConnection();
            return userList;

        }

        //select a user with a specific telegramId
        private User retrieveUserFromId(Long telegramId){
            User selectedUser = new User();
            buildConnection();
            try{

                String selectFromIdQuery = String.format("SELECT * FROM USER\n" +
                        "WHERE USER.telegramId = %d;",telegramId);
                PreparedStatement statement = connection.prepareStatement(selectFromIdQuery);
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                ResultSet rs = statement.executeQuery();

                selectedUser.telegramId = rs.getLong("telegramId");
                selectedUser.userName = rs.getString("userName");
                selectedUser.checkLevel = rs.getInt("checkLevel");

                rs.close();
                statement.close();

            }catch(SQLException e){
                e.printStackTrace();
            }
            finally {
                closeConnection();
            }

            return selectedUser;

        }

        // insert a user into USER table
        private void insertUser(Long telegramId,int checkLevel, String userName){
            buildConnection();
            try{

                String insertQuery = String.format("INSERT INTO USER(" +
                        "telegramId,checkLevel,userName)\n"+
                        "VALUES(%d,%d,'%s');",telegramId,checkLevel,userName);
                PreparedStatement statement = connection.prepareStatement(insertQuery);
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                statement.executeUpdate();
                statement.close();

            }catch(SQLException e){
                e.printStackTrace();
            }
            finally {
                closeConnection();
            }


        }

        private int insertSnapshot(String url, InputStream screenshot, String siteContentHash){
            buildConnection();
            int generatedKey = -1;
            try{
                String insertSnapshotQ= "INSERT INTO SNAPSHOT(url,screenshot,siteContentHash)" + "VALUES(?,?,?)";
                System.out.println(111);
                PreparedStatement ps = connection.prepareStatement(insertSnapshotQ,Statement.RETURN_GENERATED_KEYS);
                System.out.println(112);
                ps.setString(1,url);
                System.out.println(113);
                
                if (screenshot == null) {
                	ps.setNull(2, Types.NULL );
                	System.out.println(114);
                } else {
                	//ps.setBlob(2, screenshot);
                	//ps.setBinaryStream(2,screenshot);
                    //byte screenshotByte[] = ImageUtils.getImageUtils().getByteData(ImageUtils.getImageUtils().convertInputStreamIntoBufferedImage(screenshot));
                	ps.setBytes(2, screenshot.readAllBytes());
                	System.out.println(222);
                }
                ps.setString(3,siteContentHash);
                System.out.println(115);
                ps.executeUpdate();
                System.out.println(116);

                ResultSet genKeys = ps.getGeneratedKeys();
                if ( genKeys.next() ) {
                    generatedKey= genKeys.getInt( 1 );
                } else {
                    System.out.println("there is no generated id");
                }

                ps.close();


            }
            catch(SQLException | IOException e){
            	System.out.println(117);
                e.printStackTrace();
            }
            finally {
                closeConnection();
                System.out.println(119);
                
            }
            return generatedKey;
        }


        private InputStream retrieveImageInputStreamFromSnapshotId(int SnapshotId){
            buildConnection();
            InputStream is = null;
            try{
                String retrieveSnapshot = String.format("SELECT screenshot FROM SNAPSHOT" +
                        "WHERE SnapshotId = %d",SnapshotId);
                PreparedStatement statement = connection.prepareStatement(retrieveSnapshot);

                ResultSet rs = statement.executeQuery();
                Blob ablob = rs.getBlob("screenshot");
                is = ablob.getBinaryStream();

            }catch(SQLException e){
                e.printStackTrace();
            }
            finally {
                closeConnection();
            }

        return is;
        }

/*
        private void insertRequest(Long telegramId,String userName, int checkInterval,String url,InputStream screenshot,
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

        } */

        private boolean checkUserExists(Long telegramId){
            buildConnection();
            try{
                boolean exists;
                String checkquery = String.format("SELECT * FROM" +
                        " USER WHERE USER.telegramId = %d",telegramId);
                PreparedStatement statement = connection.prepareStatement(checkquery);

                ResultSet rs = statement.executeQuery();
                if(rs.next()){
                    exists = true;
                }
                else{
                    exists = false;
                }
                closeConnection();
                return exists;

            }catch(SQLException e){
                e.printStackTrace();
                closeConnection();
                return false;
            }

        }


    @Override
    public User retrieveUserFromId(long userId, String userName) {
        //check if user exists
        if(!checkUserExists(userId)){
            //create if not
            int checkLevel = Config.getConfig().DEFAULT_LEVEL;
            insertUser(userId,checkLevel,userName);
        }

        User myUser = retrieveUserFromId(userId);

        //return user
        return myUser;
    }

    @Override
    public ArrayList<Request> getRequests() {
        ArrayList<Request> reqList = new ArrayList<Request>();

        buildConnection();
        try{
            Statement statement = connection.createStatement();
            String selectReqs = "SELECT * FROM REQUEST";
            ResultSet rs = statement.executeQuery(selectReqs);
            while(rs.next()){
                Request myReq = new Request(rs.getInt("requestId"),rs.getLong("telegramId")
                ,rs.getInt("snapshotId"),rs.getInt("checkInterval"),rs.getLong("lastCheckUnix"),
                        rs.getBoolean("isActive"));
                reqList.add(myReq);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        closeConnection();
        return reqList;

    }

    @Override
    public Snapshot retrieveSnapshotFromId(int snapshotId) {
        Snapshot mySnapshot = new Snapshot();
        buildConnection();
        try{
            
            String getSnapshotQ = String.format("SELECT * FROM SNAPSHOT" +
                    " WHERE SNAPSHOT.snapshotId = %d",snapshotId);
            PreparedStatement ps = connection.prepareStatement(getSnapshotQ);
            ResultSet rs = ps.executeQuery();
            mySnapshot.snapshotId = rs.getInt("snapshotId");
            mySnapshot.url = rs.getString("url");
            InputStream ss = rs.getBinaryStream("screenshot");

            if (ss != null) {
                mySnapshot.screenshot = ImageUtils.getImageUtils().convertInputStreamIntoBufferedImage(ss);
            }
            String sch = rs.getString("siteContentHash");
            if (sch != null) {
                mySnapshot.siteContentHash = sch;
            }


        }catch(SQLException e){
            e.printStackTrace();
        }
        closeConnection();
        return mySnapshot;

    }

    public objects.Request retrieveRequestFromId(int requestId) {
        
        buildConnection();
        Request myRequest = null;
        try{
            String getRequestQ = String.format("SELECT * FROM REQUEST" +
                    " WHERE REQUEST.requestId = %d",requestId);
            PreparedStatement ps = connection.prepareStatement(getRequestQ);

            ResultSet rs = ps.executeQuery();
            
            myRequest = new Request(rs.getInt("requestId"),
	            		rs.getLong("telegramId"),
	            		rs.getInt("snapshotId"),
	            		rs.getInt("checkInterval"),
	            		rs.getLong("lastCheckUnix"),
	            		rs.getBoolean("isActive")
            		);
            
            
//            myRequest.requestId = rs.getInt("requestId");
//            myRequest.snapshotId = rs.getInt("snapshotId");
//            myRequest.checkInterval = rs.getInt("checkInterval");
//            myRequest.telegramId = rs.getLong("telegramId");
//            myRequest.isActive = rs.getBoolean("isActive");
//            myRequest.lastCheckedUnix = rs.getLong("lastCheckUnix");

        }catch(SQLException e){
            e.printStackTrace();
        }
        closeConnection();
        return myRequest;
    }

	// Requests.telegramId,   Requests.LastCheckUnix, Snapshot.url, Snapshot.screenshot, Snapshot.siteContentHash
	public boolean addRequest(Long chatId, long epochSecond, String url2, BufferedImage screenshot,
			String siteContentHash) {
        int generatedKey = -1;
		
        try{
            // insert snapshot and get the id
            int snapshotId= insertSnapshot(url2,ImageUtils.getImageUtils().convertBufferedImageIntoInputStream(screenshot),siteContentHash);
            System.out.println("Inserted snapshot");

            boolean isActive = true;
            int isActiveInt = (isActive)? 1 : 0;

            System.out.println("Got snapshot id: " + snapshotId);

            //statement.setQueryTimeout(30);  // set timeout to 30 sec.
            String insertReqUpdate = String.format("INSERT INTO REQUEST" +
                    "(telegramId,snapshotId,checkInterval,lastCheckUnix,isActive) VALUES" +
                    "(%d,%d,%d,%d,%d)",chatId,snapshotId,Config.getConfig().DEFAULT_LEVEL, epochSecond,isActiveInt);
            buildConnection();
            PreparedStatement statement = connection.prepareStatement(insertReqUpdate, Statement.RETURN_GENERATED_KEYS);



            statement.executeUpdate();
            System.out.println("Inserted Request");


            ResultSet genKeys = statement.getGeneratedKeys();
            if ( genKeys.next() ) {
                generatedKey= genKeys.getInt( 1 );
            } else {
                System.out.println("there is no generated id");
            }

            objects.Request req = retrieveRequestFromId(generatedKey);
            MultiprocessingUtils.getMultiProcessingUtils().submitUpnotify(UpnotifyBot.getUpnotifyBot(), req);

        }catch(Exception e){
            e.printStackTrace();
            closeConnection();
            return false;
        }

		closeConnection();

		return true;
		
	}
	
	public boolean editSnapshot(Snapshot snap) {
		buildConnection();
        
        try{
            String editSnapshotQ = "UPDATE SNAPSHOT SET url = ? , "
            						+ "screenshot = ? , "
            						+ "siteContentHash = ? "
            						+ "WHERE snapshotId = ?";
           
            PreparedStatement ps = connection.prepareStatement(editSnapshotQ);
            ps.setString(1, snap.url);
            
            if (snap.screenshot == null) {
            	ps.setNull(2, Types.NULL);
            } else {
            	ps.setBytes(2, ImageUtils.getImageUtils().convertBufferedImageIntoInputStream(snap.screenshot).readAllBytes());
            }
            ps.setString(3, snap.siteContentHash);
            ps.setInt(4, snap.snapshotId);
            ps.executeUpdate();
            ps.close();
        }
        catch(SQLException | IOException e){
            e.printStackTrace();
            closeConnection();
    		return false;
        }
        
		closeConnection();
		return true;
	}

	public boolean editRequest(Request req, Snapshot snap) {
		buildConnection();
		boolean success = false;
		try{
			//I didn't remove try catch blocks since checkInterval can made to be changed in future versions
			
            /*Statement statement = connection.createStatement();
            

            boolean isActive = true;
            int isActiveInt = (isActive)? 1 : 0;
             */
			
			System.out.println("Snapshot id is being edited: " + snap.snapshotId);
			success = editSnapshot(snap);

            /*String updateSnapQ = String.format("INSERT INTO REQUEST" +
                    "(telegramId,snapshotId,checkInterval,lastCheckUnix,isActive) VALUES" +
                    "(%d,%d,%d,%d,%d)",0,0,0,0);
            statement.executeUpdate(updateSnapQ);
            */
           
            
        }catch(Exception e){
        	System.err.println(e.getMessage());
            closeConnection();
            return false;
        }

		System.out.println("Edited Request, id " + req.requestId);
		closeConnection();
		return success;
	}

	public boolean removeRequest(Request req) {
        buildConnection();
        try {
            String removeRequest = "DELETE FROM REQUEST WHERE requestId = ?";
            PreparedStatement ps = connection.prepareStatement(removeRequest);
            ps.setInt(1, req.requestId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean removeUser(User user) {
        buildConnection();
        try {
            String removeUser = "DELETE FROM USER WHERE telegramId = ?";
            PreparedStatement ps = connection.prepareStatement(removeUser);
            ps.setLong(1, user.telegramId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean removeSnapshotFromId(int ssId) {
        buildConnection();
        try {
            String removeSnapshot = "DELETE FROM SNAPSHOT WHERE snapshotId = ?";
            PreparedStatement ps = connection.prepareStatement(removeSnapshot);
            ps.setInt(1, ssId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }

	public boolean removeAllRequests() {
        buildConnection();
        try {
            Statement statement = connection.createStatement();

            String removeRequest = "DELETE FROM REQUEST";
            statement.executeUpdate(removeRequest);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;

    }

    public boolean removeAllUsers() {
        buildConnection();
        try {
            Statement statement = connection.createStatement();

            String removeUser = "DELETE FROM USER";
            statement.executeUpdate(removeUser);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;

    }

    public boolean removeAllSnapshots() {
        buildConnection();
        try {
            Statement statement = connection.createStatement();

            String removeSnapshot = "DELETE FROM SNAPSHOT";
            statement.executeUpdate(removeSnapshot);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean cleanDatabase() {
        buildConnection();
        try {
            Statement statement = connection.createStatement();

            String removeSnapshot = "DELETE FROM SNAPSHOT";
            String removeUser = "DELETE FROM USER";
            String removeRequest = "DELETE FROM REQUEST";

            statement.executeUpdate(removeSnapshot);
            statement.executeUpdate(removeRequest);
            statement.executeUpdate(removeUser);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }

    public boolean dropTables() {
        buildConnection();
        try {
            Statement statement = connection.createStatement();

            String dropUserTable = "DROP TABLE USER";
            String dropSnapshotTable = "DROP TABLE SNAPSHOT";
            String dropRequestTable = "DROP TABLE REQUEST";
            statement.executeUpdate(dropUserTable);
            statement.executeUpdate(dropSnapshotTable);
            statement.executeUpdate(dropRequestTable);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }

	public ArrayList<Request> getRequestsFromTelegramId(Long telegramId) {
		// TODO Auto-generated method stub
        ArrayList<Request> reqList = new ArrayList<Request>();

        buildConnection();
        try{
    
            String selectReqs = "SELECT * FROM REQUEST WHERE telegramId = ?";
            PreparedStatement ps = connection.prepareStatement(selectReqs);
            ps.setLong(1, telegramId);
            
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Request myReq = new Request(rs.getInt("requestId"),rs.getLong("telegramId")
                ,rs.getInt("snapshotId"),rs.getInt("checkInterval"),rs.getLong("lastCheckUnix"),
                        rs.getBoolean("isActive"));
                reqList.add(myReq);
                System.out.println(myReq.toString());
            }
            
        }catch(SQLException e){
            e.printStackTrace();
        }
        closeConnection();
        return reqList;
	}
	public boolean removeRequestFromId(int requestId, Long telegramId) {
        buildConnection();
        try {
            // String query = "SELECT FROM REQUEST WHERE telegramId = ?";
            // PreparedStatement ps = connection.prepareStatement(query);
            // ps.setLong(1, telegramId);
            // if (telegramId)
            // System.out.println("rrfi1");
            String removeRequest = "DELETE FROM REQUEST WHERE requestId = ? AND telegramId = ?";
            // System.out.println("rrfi2");
            PreparedStatement ps = connection.prepareStatement(removeRequest);
            // System.out.println("rrfi3");
            ps.setInt(1, requestId);
            // System.out.println("rrfi4");
            ps.setLong(2, telegramId);
            // System.out.println("rrfi5");
            int aa = ps.executeUpdate(); // if db is updated 1, if not 0
            // System.out.println("rrfi6");
            System.out.println("heyho" + aa);
            if (aa < 1) {
                closeConnection();
                return false;
            }
            // remove req from thread pool if it was active
            
            MultiprocessingUtils.getMultiProcessingUtils().removeUpnotify(requestId);
            

        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
       
	}
    private boolean fixPragmaWAL(){
        buildConnection();
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("pragma journal_mode=WAL;");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }
    
    public boolean getRequestActiveFromId(int id) {
        buildConnection();
        boolean ret = false;
        try{
            String select = "SELECT isActive FROM REQUEST WHERE requestId = ? ";
            PreparedStatement ps = connection.prepareStatement(select);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            


            while(rs.next()){
                ret = rs.getBoolean("isActive");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }


        closeConnection();
        return ret;



    }
    /**
     * 
     * @param num id of request
     * @param telegramId
     * @return
     */
	public boolean toggleRequestFromId(int num, Long telegramId) {
        boolean h = getRequestActiveFromId(num);
        buildConnection();
        try {

            

            String toggleRequest = "UPDATE REQUEST SET isActive = ? WHERE requestId = ? AND telegramId = ?";

            PreparedStatement ps = connection.prepareStatement(toggleRequest);
            ps.setInt(1, h ? 0 : 1);

            ps.setInt(2, num);
            ps.setLong(3, telegramId);
            int aa = ps.executeUpdate();
            System.out.println("aa="+ aa);
            
            if (aa < 1) {
                closeConnection();
                return false;
            }
            

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        if (h) {
            // request was active and now is passive, so remove from mapee
            MultiprocessingUtils.getMultiProcessingUtils().removeUpnotify(num);
        } else {
            MultiprocessingUtils.getMultiProcessingUtils().submitUpnotify(UpnotifyBot.getUpnotifyBot(), retrieveRequestFromId(num) );

        }
        return true;
        
	}

	public ArrayList<Integer> getSnapshotIds() {
        ArrayList<Integer> ids = new ArrayList<Integer>();

        buildConnection();
        try{
            Statement statement = connection.createStatement();
            String select = "SELECT snapshotId FROM SNAPSHOT";
            ResultSet rs = statement.executeQuery(select);
            while(rs.next()){
                ids.add(rs.getInt("snapshotId"));
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        closeConnection();
        return ids;
	}
}