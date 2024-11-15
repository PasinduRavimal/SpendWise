package com.spendWise.models;

import java.sql.Statement;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.spendWise.controllers.DashboardContentController;
import com.spendWise.util.DatabaseConnection;

public class UserAccountModel extends UserAccount {
    private static volatile UserAccountModel instance;

    private String username;
    private String displayName;
    private String password;

    private UserAccountModel(String username, String displayName, String password) {

        this.username = username;
        this.displayName = displayName;
        this.password = password;
    }

    static synchronized UserAccountModel newUserAccount(String username, String displayName, String password)
            throws SQLException, IllegalStateException, IllegalArgumentException, IOException {
        if (instance != null) {
            throw new IllegalStateException("Please log out first.");
        }

        if (UserAccount.isUserExist(username)) {
            throw new IllegalArgumentException("User already exists.");
        }

        UserAccountModel userAccount = new UserAccountModel(username, displayName, password);
        userAccount.saveToDatabase();
        instance = userAccount;
        return instance;
    }

    static synchronized UserAccount getUserAccount(String username, String password)
            throws SQLException, IOException, IllegalStateException {

        if (instance != null) {
            throw new IllegalStateException("Please log out first.");
        }

        try {
            DatabaseConnection.getConnection();
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username, password);
            var rs = ps.executeQuery();
            if (rs.next()) {
                instance = new UserAccountModel(rs.getString("username"), rs.getString("displayname"),
                        rs.getString("password"));
                rs.close();
                ps.close();
                return instance;
            }
            rs.close();
            ps.close();
            return null;

        } catch (IOException e) {
            throw new IOException("Cannot access critical files.");
        }
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized String getDisplayName() {
        return displayName;
    }

    public static synchronized UserAccountModel getInstance() {
        return instance;
    }

    synchronized boolean saveToDatabase() throws SQLException, IOException {
        try {
            DatabaseConnection.getConnection();
            String query = "INSERT INTO Users (username, displayName, password) VALUES (?, ?, ?)";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username, displayName, password);
            ps.executeUpdate();
            ps.close();
            return true;

        } catch (IOException e) {
            throw new IOException("Cannot access critical files.");
        }
    }

    synchronized boolean updateDatabase() throws SQLException, IOException {
        try {
            DatabaseConnection.getConnection();
            String query = "UPDATE Users SET displayName = ?, password = ? WHERE username = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, displayName, password, username);
            ps.executeUpdate();
            ps.close();
            return true;

        } catch (IOException e) {
            throw new IOException("Cannot access critical files.");
        }
    }

    synchronized void updateUserAccount(String newDisplayName, String newPassword) {
        displayName = newDisplayName;
        password = newPassword;

        DashboardContentController.setUserLabel(newDisplayName);
    }

    public synchronized boolean deleteUserAccount() throws SQLException, IOException {
        try {
            DatabaseConnection.getConnection();
            Statement stmt = DatabaseConnection.getStatement();
            stmt.addBatch("DELETE FROM transactions WHERE username = '" + username + "'");
            stmt.addBatch("DELETE FROM generaljournal WHERE username = '" + username + "'");
            stmt.addBatch("DELETE FROM accounttypes WHERE accountOwner = '" + username + "'");
            stmt.addBatch("DELETE FROM users WHERE username = '" + username + "'");
            stmt.executeBatch();
            stmt.close();

            this.username = null;
            this.displayName = null;
            this.password = null;

            return true;

        } catch (IOException e) {
            throw new IOException("Need permission to access file system.");
        }
    }

    public static synchronized void logout() {
        if (instance == null) {
            throw new IllegalStateException("No user logged in.");
        }

        instance = null;
    }

    public void changePassword(String newPassword) throws SQLException, IOException {
        updateUserAccount(displayName, newPassword);
        updateDatabase();
    }

    public void changeDisplayName(String displayName) throws SQLException, IOException {
        updateUserAccount(displayName, password);
        updateDatabase();
    }

    public boolean validateCurrentPassword(String password) {
        return getPassword().equals(password);
    }

}
