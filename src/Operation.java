
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class Operation {
static Connection connection;
    public static void insertData(String name, int port, String ip) {

        String sqlQuery = "INSERT INTO User ( name, port, ip) VALUES (?,?,?)";
        try {
             connection = SqliteDB.getConnection();
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(sqlQuery);
          //  preparedStatement.setInt(1, id);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, port);
            preparedStatement.setString(3, ip);
            System.out.println("Data has been inserted!");
        } catch (SQLException ex) {
            Logger.getLogger(Operation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
     public static void getData() {
   String sqlQuery = "Select name from User";
    try {
     
        PreparedStatement preparedStatement;
        preparedStatement = connection.prepareStatement(sqlQuery);
        
        ResultSet result = preparedStatement.executeQuery();
        System.out.println("preparedStatement " +result.getString("name") );
    } catch (SQLException ex) {
        Logger.getLogger(Operation.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
}
