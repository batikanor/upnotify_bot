package utils;

import java.sql.*;

public class DatabaseUtils
{
    public static Connection connection = null;


    //tablo dbde var ise true yok ise false döndüren bir fonksiyon
    public static boolean tableExists(String tableName,Connection conn){

        try{
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, tableName, null);
            rs.last();
            return rs.getRow() > 0;
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return false;
    }

    public static void create_tables(){
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/upnotify.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            // create users table if not exist
            if(!tableExists("users",connection)){
                String create_user_table = "create table users\n" +
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
                    System.out.print("Users table has created");
                } catch (SQLException e){
                    System.err.println(e.getMessage());
                }
            }
            else{
                //table allready exists
            }

        }
        catch(SQLException e){
            System.out.println("there is a problem with db connection");
            System.err.println(e.getMessage());
        }
        finally
        {
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
    }



}
