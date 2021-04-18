package utils;

import javax.xml.crypto.Data;
import java.sql.*;

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
                if(!tableExists("USERS",connection)){
                    String create_user_table = "create table USERS\n" +
                            "(\n" +
                            "\ttelegramId INTEGER\n" +
                            "\t\tconstraint USERS_pk\n" +
                            "\t\t\tprimary key autoincrement,\n" +
                            "\tcheckLevel int\n" +
                            ");\n" +
                            "\n" +
                            "create unique index USERS_telegramId_uindex\n" +
                            "\ton USERS (telegramId);\n" +
                            "\n" ;

                    try {
                        statement.executeQuery(create_user_table);
                        System.out.print("USERS table has created");
                    } catch (SQLException e){
                        System.err.println(e.getMessage());
                    }
                }
                else{
                    System.out.println("USERS table already exists");
                    //USERS table allready exists
                }
                // yok ise WEB_PAGES tablosunu oluştur
                if(!tableExists("WEB_PAGES",connection)){
                    String create_webpages_table = "create table WEB_PAGES\n" +
                            "(\n" +
                            "\tid INTEGER\n" +
                            "\t\tconstraint WEB_PAGES_pk\n" +
                            "\t\t\tprimary key autoincrement,\n" +
                            "\taddress String,\n" +
                            "\tlastChecked String\n" +
                            ");\n" +
                            "\n" +
                            "create unique index WEB_PAGES_id_uindex\n" +
                            "\ton WEB_PAGES (id);";

                    try {
                        statement.executeQuery(create_webpages_table);
                        System.out.print("WEB_PAGES table has created");
                    } catch (SQLException e){
                        System.err.println(e.getMessage());
                    }
                }else{
                    System.out.println("WEB_PAGES table already exists");
                    //WEB_PAGES table allready exists
                }

                // yok ise WEB_PAGES tablosunu oluştur
                if(!tableExists("REQUESTS",connection)){
                    String create_requests_table = "create table REQUESTS\n" +
                            "(\n" +
                            "\trequestId INTEGER\n" +
                            "\t\tconstraint REQUESTS_pk\n" +
                            "\t\t\tprimary key autoincrement,\n" +
                            "\ttelegramId int,\n" +
                            "\tsiteId int\n" +
                            ");\n" +
                            "\n" +
                            "create unique index REQUESTS_requestId_uindex\n" +
                            "\ton REQUESTS (requestId);\n";

                    try {
                        statement.executeQuery(create_requests_table);
                        System.out.print("REQUESTS table has created");
                    } catch (SQLException e){
                        System.err.println(e.getMessage());
                    }
                }else{
                    System.out.println("REQUESTS table already exists");
                    //WEB_PAGES table allready exists
                }




            }catch (SQLException e){
                System.err.println(e.getMessage());
            }


            closeConnection();

        }





}
