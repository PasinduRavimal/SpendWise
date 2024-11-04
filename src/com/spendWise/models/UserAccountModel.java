package com.spendWise.models;

import java.io.IOException;
import java.sql.SQLException;

import com.spendWise.util.DatabaseConnection;

public class UserAccountModel extends UserAccount {
    private static UserAccountModel instance;
    
    private String username;
    private String displayName;
    private String password;

    private UserAccountModel(String username, String displayName, String password) {
        this.username = username;
        this.displayName = displayName;
        this.password = password;
    }

    static UserAccountModel newUserAccount(String username, String displayName, String password) {
        if (instance != null){
            throw new IllegalStateException("Please log out first.");
        }
        instance = new UserAccountModel(username, displayName, password);
        return instance;
    }

    static UserAccount getUserAccount(String username, String password) throws SQLException {
    
        if (instance != null){
            throw new IllegalStateException("Please log out first.");
        }

        try{
            DatabaseConnection.getConnection();
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            var rs = DatabaseConnection.getPreparedStatement(query, username, password).executeQuery();
            if (rs.next()){
                return new UserAccountModel(rs.getString("username"), rs.getString("displayname"), rs.getString("password"));
            }
            return null;

        } catch(IOException e){
            return null;
        } finally{
            DatabaseConnection.getStatement().close();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    boolean saveToDatabase() throws SQLException{
        try{
            DatabaseConnection.getConnection();
            String query = "INSERT INTO Users (username, displayName, password) VALUES (?, ?, ?)";
            DatabaseConnection.getPreparedStatement(query, username, displayName, password).executeUpdate();
            return true;

        } catch(IOException e){
            return false;
        } finally{
            DatabaseConnection.getStatement().close();
        }
    }

    boolean updateDatabase() throws SQLException {
        try{
            DatabaseConnection.getConnection();
            String query = "UPDATE Users SET displayName = ?, password = ? WHERE username = ?";
            DatabaseConnection.getPreparedStatement(query, username, displayName, password).executeUpdate();
            return true;

        } catch(IOException e){
            return false;
        } finally{
            DatabaseConnection.getStatement().close();
        }
    }

    void updateUserAccount(String newDisplayName, String newPassword) {
        displayName = newDisplayName;
        password = newPassword;
    }

    boolean deleteUserAccount(String username) throws SQLException{
        try{
            DatabaseConnection.getConnection();
            String query = "DELETE FROM Users WHERE username = ?";
            DatabaseConnection.getPreparedStatement(query, username).executeUpdate();

            this.username = null;
            this.displayName = null;
            this.password = null;

            return true;
            
        } catch(IOException e){
            return false;
        } finally {
            DatabaseConnection.getStatement().close();
        }
    }

}
