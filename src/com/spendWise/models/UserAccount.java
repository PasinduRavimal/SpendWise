package com.spendWise.models;

import java.sql.*;

import com.spendWise.util.*;

public abstract class UserAccount {

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

    public static boolean signup(String username, String displayName, String password) {
        try {
            UserAccountModel user = UserAccountModel.newUserAccount(username, displayName, password);
            user.saveToDatabase();
            return true;
        } catch (SQLException e){
            return false;
        }

    }

    public static UserAccount login(String username, String password) {
        try {
            return UserAccountModel.getUserAccount(username, password);
        } catch (SQLException e){
            return null;
        }
    }

    public static boolean isUserExist(String username) throws SQLException {
        
        try {
            DatabaseConnection.getConnection();
            String query = "SELECT * FROM Users WHERE username = ?";
            ResultSet rs = DatabaseConnection.getPreparedStatement(query, username).executeQuery();
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
}
