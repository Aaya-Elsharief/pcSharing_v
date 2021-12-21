
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author hp
 */
public class SqliteDB {

    public static Connection connection = null;
    public static String sqliteServer = "jdbc:sqlite:";
    public static String Path = "jdbc:sqlite:.\\sqliteDB";

    public static boolean isDataBaseExists(String dbFilePath) {
        File dbFile = new File(dbFilePath);
        return dbFile.exists();
    }

    public static Connection getConnection() {
     //   String getFilePath = new File(Path).getAbsolutePath();

      
            try {
                connection = DriverManager.getConnection(Path);
            } catch (SQLException ex) {
                Logger.getLogger(SqliteDB.class.getName()).log(Level.SEVERE, null, ex);
            }

       System.out.println("Opened database successfully");
       createTable();
       return connection;
    }
    
    
    
    


    private static void createTable() {
        try {
            // SQL statement for creating a new table
            String sql = "CREATE TABLE IF NOT EXISTS User (\n"
                    + "	id integer PRIMARY KEY,\n"
                    + "	name text NOT NULL,\n"
                    + "	port int,\n"
                    + "	ip string\n"
                    + ");";
            
            
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException ex) {
            Logger.getLogger(SqliteDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Table created");
    }

}
