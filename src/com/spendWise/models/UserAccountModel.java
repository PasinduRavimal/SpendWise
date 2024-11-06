package com.spendWise.models;

import java.io.IOException;
import java.sql.SQLException;

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

        instance = new UserAccountModel(username, displayName, password);
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
            var rs = DatabaseConnection.getPreparedStatement(query, username, password).executeQuery();
            if (rs.next()) {
                instance = new UserAccountModel(rs.getString("username"), rs.getString("displayname"),
                        rs.getString("password"));
                return instance;
            }
            return null;

        } catch (IOException e) {
            throw new IOException("Cannot access critical files.");
        } finally {
            DatabaseConnection.getStatement().close();
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

    synchronized boolean saveToDatabase() throws SQLException, IOException {
        try {
            DatabaseConnection.getConnection();
            String query = "INSERT INTO Users (username, displayName, password) VALUES (?, ?, ?)";
            DatabaseConnection.getPreparedStatement(query, username, displayName, password).executeUpdate();
            return true;

        } catch (IOException e) {
            throw new IOException("Cannot access critical files.");
        } finally {
            DatabaseConnection.getStatement().close();
        }
    }

    synchronized boolean updateDatabase() throws SQLException, IOException {
        try {
            DatabaseConnection.getConnection();
            String query = "UPDATE Users SET displayName = ?, password = ? WHERE username = ?";
            DatabaseConnection.getPreparedStatement(query, username, displayName, password).executeUpdate();
            return true;

        } catch (IOException e) {
            throw new IOException("Cannot access critical files.");
        } finally {
            DatabaseConnection.getStatement().close();
        }
    }

    synchronized void updateUserAccount(String newDisplayName, String newPassword) {
        displayName = newDisplayName;
        password = newPassword;
    }

    synchronized boolean deleteUserAccount(String username) throws SQLException, IOException {
        try {
            DatabaseConnection.getConnection();
            String query = "DELETE FROM Users WHERE username = ?";
            DatabaseConnection.getPreparedStatement(query, username).executeUpdate();

            this.username = null;
            this.displayName = null;
            this.password = null;

            return true;

        } catch (IOException e) {
            throw new IOException("Need permission to access file system.");
        } finally {
            DatabaseConnection.getStatement().close();
        }
    }

    public static synchronized void logout() {
        if (instance == null) {
            throw new IllegalStateException("No user logged in.");
        }

        instance = null;
    }

}
