package com.spendWise.models;

import java.io.IOException;
import java.sql.*;

import com.spendWise.util.*;

public abstract class UserAccount {

    public static boolean doUsersExist() throws SQLException, IOException {
        
        try {
            DatabaseConnection.getConnection();
            ResultSet rs = DatabaseConnection.getStatement().executeQuery("SELECT * FROM Users");
            if (!rs.next()){
                return false;
            }
            return true;

        } catch (IOException e) {
            throw new IOException("Cannot access critical files.");
        } finally {
            DatabaseConnection.getStatement().close();
        }
    }

    public static boolean signup(String username, String displayName, String password) throws IOException, SQLException {
        try {
            UserAccountModel user = UserAccountModel.newUserAccount(username, displayName, password);
            user.saveToDatabase();
            return true;
        } catch (SQLException e){
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }
            return false;
        }

    }

    public static void logout() {
        UserAccountModel.logout();
    }

    public static UserAccount login(String username, String password) throws IOException, IllegalStateException, SQLException {
        try {
            return UserAccountModel.getUserAccount(username, password);
        } catch (SQLException e){
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }
            return null;
        }
    }

    public static boolean isUserExist(String username) throws SQLException, IOException {
        
        try {
            DatabaseConnection.getConnection();
            String query = "SELECT * FROM Users WHERE username = ?";
            ResultSet rs = DatabaseConnection.getPreparedStatement(query, username).executeQuery();
            if (!rs.next()){
                return false;
            }
            return true;

        } catch (IOException e){
            throw new IOException("Cannot access critical files.");
        } finally {
            DatabaseConnection.getStatement().close();
        }
    }
}
