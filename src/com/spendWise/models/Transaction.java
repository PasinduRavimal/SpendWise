package com.spendWise.models;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
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
        if (UserAccount.getCurrentUser() == null) {
            throw new IllegalStateException("No user logged in.");
        }
        List<Transaction> transactions = new ArrayList<>();
        try {
            DatabaseConnection.getConnection();
            String query = "SELECT * FROM transactions WHERE username = ? ORDER BY transactionID DESC LIMIT ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, UserAccount.getCurrentUser().getUsername(), n);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction transaction = new TransactionModel(rs.getInt("transactionID"), rs.getString("username"), rs.getInt("debitAccountID"), rs.getInt("creditAccountID"), rs.getTimestamp("transactionTime"), rs.getDouble("amount"), rs.getString("description"));
                transactions.add(transaction);
            }
            rs.close();
            ps.close();
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
        }
    }

    public static List<Transaction> getTransactionsByAccountsAndDate(Account debitAccount, Account creditAccount, Timestamp date) throws SQLException, IOException {
        if (UserAccount.getCurrentUser() == null) {
            throw new IllegalStateException("No user logged in.");
        }
        List<Transaction> transactions = new ArrayList<>();
        try {
            DatabaseConnection.getConnection();
            String query = "SELECT * FROM transactions WHERE username = ? AND debitAccountID = ? AND creditAccountID = ? AND DATE(transactionTime) = ? ORDER BY transactionID";
            LocalDate localDate = date.toLocalDateTime().toLocalDate();
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, UserAccount.getCurrentUser().getUsername(), debitAccount.getAccountID(), creditAccount.getAccountID(), localDate.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction transaction = new TransactionModel(rs.getInt("transactionID"), rs.getString("username"), rs.getInt("debitAccountID"), rs.getInt("creditAccountID"), rs.getTimestamp("transactionTime"), rs.getDouble("amount"), rs.getString("description"));
                transactions.add(transaction);
            }
            rs.close();
            ps.close();
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
        }
    }

    public static Transaction getNewTransaction(String username, int debitingAccountID, int creditingAccountID, Timestamp transactionTime,
    double amount, String description){
        return new TransactionModel(username, debitingAccountID, creditingAccountID, transactionTime, amount, description);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof Transaction) {
            Transaction trans = (Transaction) obj;
            if (trans.getTransactionID() == this.getTransactionID()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getTransactionID() + 
        this.getUsername().hashCode() +
        this.getDebitingAccountID() +
        this.getCreditingAccountID() +
        this.getTransactionTime().hashCode() +
        this.getDescription().hashCode();
    }

}
