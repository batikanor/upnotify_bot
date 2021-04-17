package utils;

import java.sql.*;

public class DatabaseUtils
{
    public static Connection connection = null;
    public static String url = "jdbc:sqlite:src/main/resources/upnotify.db";

    private DatabaseUtils(){
    }

    public static void buildConnection(){
        try {
            connection = DriverManager.getConnection(url);
        }
        catch(SQLException e){
            System.out.println("there is a problem with db connection");
            System.err.println(e.getMessage());
        }
    }

    public static void close_connection(){
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
    public static boolean tableExists(String tableName,Connection conn){
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

    public static void create_tables(){
            buildConnection();
            try{
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.

                // yok ise USERS tablosunu oluştur
                if(!tableExists("USERS",connection)){
                    String create_user_table = "create table USERS\n" +
                            "                    (\n" +
                            "                            id integer not null\n" +
                            "            constraint users_pk\n" +
                            "            primary key autoincrement,\n" +
                            "                    userName text\n" +
                            ");\n" +
                            "\n" +
                            "            create unique index users_id_uindex\n" +
                            "            on users (id);" ;

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
                            "\tid int\n" +
                            "\t\tconstraint WEB_PAGES_pk\n" +
                            "\t\t\tprimary key autoincrement\n" +
                            ");\n" +
                            "\n" +
                            "create unique index WEB_PAGES_id_uindex\n" +
                            "\ton WEB_PAGES (id);\n";

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



            }catch (SQLException e){
                System.err.println(e.getMessage());
            }

            close_connection();

        }





}
