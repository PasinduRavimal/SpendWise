package com.spendWise.models;

import java.sql.*;

import org.mariadb.jdbc.export.ExceptionFactory.SqlExceptionFactory;

import com.spendWise.util.DatabaseConnection;

public class TransactionModel {
    private int transactionID;
    private String username;
    private int debitingAccountID;
    private int creditingAccountID;
    private Timestamp transactionTime;
    private double amount;
    private String description;

    public TransactionModel(String username, int debitingAccountID, int creditingAccountID, Timestamp transactionTime, double amount, String description){
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

    public boolean isValidateTransaction() {
        return amount > 0;
    }


    public void saveTransaction() throws SQLException {
        Statement stmt = DatabaseConnection.getStatement();
        String query = "INSERT INTO transactions (username, debitingAccountID, creditingAccountID, transactionTime, amount, description) VALUES ('" + username + "', " + debitingAccountID + ", " + creditingAccountID + ", '" + transactionTime + "', " + amount + ", '" + description + "')";
        stmt.executeUpdate(query);    
    }
}
