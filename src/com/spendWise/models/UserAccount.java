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
}
