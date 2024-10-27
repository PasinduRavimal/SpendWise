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
}
