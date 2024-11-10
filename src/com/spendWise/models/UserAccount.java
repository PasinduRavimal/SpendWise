package com.spendWise.models;

import java.io.IOException;
import java.sql.*;

import com.spendWise.controllers.DashboardContentController;
import com.spendWise.util.*;

public abstract class UserAccount {

    public abstract String getUsername();
    public abstract String getDisplayName();
    public abstract String getPassword();
    abstract boolean saveToDatabase() throws SQLException, IOException;
    abstract boolean updateDatabase() throws SQLException, IOException;
    abstract void updateUserAccount(String displayName, String password);
    abstract boolean deleteUserAccount() throws SQLException, IOException;

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

            Account.updateCurrentUser(user);

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

        Account.resetCurrentUser();
    }

    public static UserAccount login(String username, String password) throws IOException, IllegalStateException, SQLException {
        try {
            UserAccount account = UserAccountModel.getUserAccount(username, password);

            Account.updateCurrentUser(account);
            DashboardContentController.setUserLabel(account.getDisplayName());

            return account;

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
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()){
                rs.close();
                ps.close();
                return false;
            }
            rs.close();
            ps.close();
            return true;

        } catch (IOException e){
            throw new IOException("Cannot access critical files.");
        }
    }

    public static UserAccount getCurrentUser() {
        return UserAccountModel.getInstance();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof UserAccount) {
            UserAccount account = (UserAccount) obj;
            return account.getUsername().equals(this.getUsername());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getUsername().hashCode();
    }

    public static void changePassword(String confirmPassword) throws SQLException, IOException {
        UserAccountModel userAccountModel = UserAccountModel.getInstance();
        userAccountModel.updateUserAccount(userAccountModel.getDisplayName(), confirmPassword);
        userAccountModel.updateDatabase();
    }
    public static void changeDisplayName(String displayName) throws SQLException, IOException {
        UserAccountModel userAccountModel = UserAccountModel.getInstance();
        userAccountModel.updateUserAccount(displayName, userAccountModel.getPassword());
        userAccountModel.updateDatabase();
    }
    public static void deleteUser() throws SQLException, IOException {
        UserAccountModel userAccountModel = UserAccountModel.getInstance();
        userAccountModel.deleteUserAccount();
    }
    public static boolean validateCurrentPassword(String password) {
        UserAccountModel userAccountModel = UserAccountModel.getInstance();
        return userAccountModel.getPassword().equals(password);
    }

}
