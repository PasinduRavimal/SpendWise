package com.spendWise.models;

import java.sql.*;

import com.spendWise.util.*;

public interface UserAccount {

    public UserAccount getUserAccount(String username, String password) throws SQLException;

    public static boolean doUsersExist() throws SQLException {
        
        try {
            DatabaseConnection.getConnection();
            ResultSet rs = DatabaseConnection.getStatement().executeQuery("SELECT * FROM Users");
            if (!rs.next()){
                return false;
            }
            return true;

        } catch (Exception e){
            return false;
        } finally {
            DatabaseConnection.getStatement().close();
        }
    }

    public static boolean createUserAccount(String userName, String displayName, String password) throws SQLException{
        try{
            DatabaseConnection.getConnection();
            String query = "INSERT INTO Users (username, displayName, password) VALUES ('"+userName+"', '"+displayName+"', '"+password+"')";
            DatabaseConnection.getStatement().executeUpdate(query);
            return true;

        } catch(Exception e){
            return false;
        } finally{
            DatabaseConnection.getStatement().close();
        }
    }

    public static boolean updateUserAccount(String username, String newDisplayName) throws SQLException{
        try{
            DatabaseConnection.getConnection();
            String query = "UPDATE Users SET displayName = '"+newDisplayName+"' WHERE username = '"+username+"'";
            DatabaseConnection.getStatement().executeUpdate(query);
            return true;

        } catch(Exception e){
            return false;
        } finally{
            DatabaseConnection.getStatement().close();
        }
    }

    public static boolean deleteUserAccount(String username) throws SQLException{
        try{
            DatabaseConnection.getConnection();
            String query = "DELETE FROM Users WHERE username = '"+username+"'";
            DatabaseConnection.getStatement().executeUpdate(query);
            return true;
            
        } catch(Exception e){
            return false;
        } finally {
            DatabaseConnection.getStatement().close();
        }
    }
}
