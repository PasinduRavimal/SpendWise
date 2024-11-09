package com.spendWise.models;

import java.util.*;
import java.sql.*;

import com.spendWise.util.DatabaseConnection;

public abstract class Transaction {

    public abstract int getTransactionID();
    public abstract String getUsername();
    public abstract int getDebitingAccountID();
    public abstract int getCreditingAccountID();
    public abstract Timestamp getTransactionTime();
    public abstract double getAmount();
    public abstract String getDescription();
    public abstract int setDebitingAccountID(int accountID);
    public abstract int setCreditingAccountID(int accountID);
    public abstract Timestamp setTransactionTime(Timestamp time);
    public abstract double setAmount(double amount);
    public abstract String setDescription(String description);
    public abstract boolean isValidTransaction();
    public abstract void saveTransaction() throws SQLException;
    public abstract void deleteTransaction() throws SQLException;
    public abstract void updateTransaction() throws SQLException;

    public static List<Transaction> getCreditTransactions(Account account, int month, int Year)
            throws SQLException {
        try {
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactions WHERE username = ? AND creditAccountID = ? AND MONTH(transactionTime) = ? AND YEAR(transactionTime) = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username,
                    account.getAccountID(), month, Year);
            ResultSet rs = ps.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(new TransactionModel(username, rs.getInt("debitAccountID"),
                        rs.getInt("creditAccountID"), rs.getTimestamp("transactionTime"), rs.getDouble("amount"),
                        rs.getString("description")));
            }
            rs.close();
            ps.close();

            return transactions;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }
            throw e;
        }
    }

    public static List<Transaction> getDebitTransactions(Account account, int month, int Year)
            throws SQLException {
        try {
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactions WHERE username = ? AND debitAccountID = ? AND MONTH(transactionTime) = ? AND YEAR(transactionTime) = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username,
                    account.getAccountID(), month, Year);
            ResultSet rs = ps.executeQuery();

            List<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(new TransactionModel(username, rs.getInt("debitAccountID"),
                        rs.getInt("creditAccountID"), rs.getTimestamp("transactionTime"), rs.getDouble("amount"),
                        rs.getString("description")));
            }
            rs.close();
            ps.close();

            return transactions;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }
            throw e;
        }
    }

}
