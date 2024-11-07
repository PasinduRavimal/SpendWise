package com.spendWise.models;

public class AccountModel extends Account {
    private int accountID;
    private String accountName;

    public AccountModel(int accountID, String accountName){
        this.accountID = accountID;
        this.accountName = accountName;
    }

    public int getAccountID() {
        return accountID;
    }

    public String getAccountName() {
        return accountName;
    }
}
