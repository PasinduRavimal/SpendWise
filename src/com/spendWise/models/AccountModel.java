package com.spendWise.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;

import com.spendWise.util.DatabaseConnection;

public class AccountModel extends Account {
    private int accountID;
    private String accountName = null;

    public AccountModel(int accountID, String accountName) {
        this.accountID = accountID;
        this.accountName = accountName;
    }

    protected AccountModel(String accountName) throws SQLException {
        this.accountName = accountName;
        addToDatabase();
    }

    public int getAccountID() {
        return accountID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) throws SQLException {
        this.accountName = accountName;

        try {
            String query = "UPDATE accounttypes SET accountName = ? WHERE accountID = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, accountName, accountID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    public void deleteAccount() throws SQLException {
        try {
            String query = "DELETE FROM accounttypes WHERE accountID = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, accountID);
            ps.executeUpdate();
            ps.close();
            this.accountName = null;
            this.accountID = -1;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    void addToDatabase() throws SQLException {
        try {
            String query = "INSERT INTO accounttypes (accountName, accountOwner) VALUES (?, ?)";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, accountName,
                    UserAccount.getCurrentUser().getUsername());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

}
