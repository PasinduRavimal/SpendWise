package com.spendWise.models;

import java.sql.*;

import com.spendWise.util.DatabaseConnection;

public class TransactionModel {
    private int transactionID;
    private String username;
    private int debitingAccountID;
    private int creditingAccountID;
    private Timestamp transactionTime;
    private double amount;
    private String description;

    public TransactionModel(String username, int debitingAccountID, int creditingAccountID, Timestamp transactionTime,
            double amount, String description) {
        this.username = username;
        this.debitingAccountID = debitingAccountID;
        this.creditingAccountID = creditingAccountID;
        this.transactionTime = transactionTime;
        this.amount = amount;
        this.description = description;
    }

    public TransactionModel(int transactionID, String username, int debitingAccountID, int creditingAccountID,
            Timestamp transactionTime, double amount, String description) {
        this.transactionID = transactionID;
        this.username = username;
        this.debitingAccountID = debitingAccountID;
        this.creditingAccountID = creditingAccountID;
        this.transactionTime = transactionTime;
        this.amount = amount;
        this.description = description;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public String getUsername() {
        return username;
    }

    public int getDebitingAccountID() {
        return debitingAccountID;
    }

    public int getCreditingAccountID() {
        return creditingAccountID;
    }

    public Timestamp getTransactionTime() {
        return transactionTime;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public int setDebitingAccountID(int debitingAccountID) {
        return this.debitingAccountID = debitingAccountID;
    }

    public int setCreditingAccountID(int creditingAccountID) {
        return this.creditingAccountID = creditingAccountID;
    }

    public Timestamp setTransactionTime(Timestamp transactionTime) {
        return this.transactionTime = transactionTime;
    }

    public double setAmount(double amount) {
        return this.amount = amount;
    }

    public String setDescription(String description) {
        return this.description = description;
    }

    public boolean isValidTransaction() {
        return amount > 0; // TODO: Compare balances using AccountModel.getBalance()
    }

    public void saveTransaction() throws SQLException {
        try {
            String query = "INSERT INTO transactions (username, debitingAccountID, creditingAccountID, transactionTime, amount, description) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username, debitingAccountID,
                    creditingAccountID, transactionTime, amount, description);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Cannot connect to the database");
                } else if (e.getErrorCode() == 1045) {
                    throw new SQLException(
                            "Please provide the correct database credentials in the databaseInfo.properties file.");
                }
            }
            throw e;
        }
    }

    public void deleteTransaction() throws SQLException {
        try {
            String query = "DELETE FROM transactions WHERE transactionID = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, transactionID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Cannot connect to the database");
                } else if (e.getErrorCode() == 1045) {
                    throw new SQLException(
                            "Please provide the correct database credentials in the databaseInfo.properties file.");
                }
            }
            throw e;
        }
    }

    public void updateTransaction() throws SQLException {
        try {
            String query = "UPDATE transactions SET debitingAccountID = ?, creditingAccountID = ?, transactionTime = ?, amount = ?, description = ? WHERE transactionID = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, debitingAccountID, creditingAccountID,
                    transactionTime, amount, description, transactionID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Cannot connect to the database");
                } else if (e.getErrorCode() == 1045) {
                    throw new SQLException(
                            "Please provide the correct database credentials in the databaseInfo.properties file.");
                }
            }
            throw e;
        }
    }

}
