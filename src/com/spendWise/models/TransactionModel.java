package com.spendWise.models;

import java.sql.*;

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

    public void displayTransactionDetails() {
        System.out.println("Transaction ID: " + transactionID);
        System.out.println("Username: " + username);
        System.out.println("Debiting Account ID: " + debitingAccountID);
        System.out.println("Crediting Account ID: " + creditingAccountID);
        System.out.println("Transaction Time: " + transactionTime);
        System.out.println("Amount: " + amount);
        System.out.println("Description: " + description);
    }


    public boolean isValidateTransaction() {
        return amount > 0;
    }


    public void saveTransaction() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/spendwise", "root", "");
            Statement stmt = conn.createStatement();
            String query = "INSERT INTO transactions (username, debitingAccountID, creditingAccountID, transactionTime, amount, description) VALUES ('" + username + "', " + debitingAccountID + ", " + creditingAccountID + ", '" + transactionTime + "', " + amount + ", '" + description + "')";
            stmt.executeUpdate(query);
            System.out.println("Transaction Saved Successfully");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
