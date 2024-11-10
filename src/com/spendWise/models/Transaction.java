package com.spendWise.models;

import java.io.IOException;
import java.sql.*;
import java.util.*;

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

    public static List<Transaction> getLastTransactions(int n) throws SQLException, IOException {
        List<Transaction> transactions = new ArrayList<>();
        try {
            DatabaseConnection.getConnection();
            ResultSet rs = DatabaseConnection.getStatement().executeQuery("SELECT * FROM transactions ORDER BY transactionID DESC LIMIT " + n);
            while (rs.next()) {
                Transaction transaction = new TransactionModel(rs.getInt("transactionID"), rs.getString("username"), rs.getInt("debitAccountID"), rs.getInt("creditAccountID"), rs.getTimestamp("transactionTime"), rs.getDouble("amount"), rs.getString("description"));
                transactions.add(transaction);
            }
            return transactions;
        } catch (SQLException e) {
            e.printStackTrace();
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }
            throw e;
        } catch (IOException e) {
            throw new IOException("Cannot access critical files.");
        } finally {
            DatabaseConnection.getStatement().close();
        }
    }

}
