package com.spendWise.models;

import java.sql.*;
import java.util.*;

import com.spendWise.controllers.HomeController;
import com.spendWise.util.DatabaseConnection;

public abstract class Account {
    private volatile static List<Account> accounts = new ArrayList<>();
    private volatile static UserAccount currentUser;

    public abstract int getAccountID();
    public abstract String getAccountName();

    public static synchronized List<Account> getAccountsList() throws SQLException {
        if (currentUser == null) {
            return accounts;
        }

        if (!accounts.isEmpty() && currentUser.equals(UserAccount.getCurrentUser())) {
            return accounts;
        }

        accounts.clear();

        try {
            String query = "SELECT * FROM accounttypes WHERE accountOwner = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, currentUser.getUsername());
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                accounts.add(new AccountModel(rs.getInt("accountID"), rs.getString("accountName")));
            }

            rs.close();
            ps.close();

            return accounts;
        } catch (SQLException e) {
            // TODO: Remove following code
            DatabaseConnection.getStatement().executeUpdate("ALTER TABLE accounttypes ADD COLUMN accountOwner VARCHAR(50) NOT NULL");
            
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    public static synchronized void resetCurrentUser(){
        currentUser = null;
        accounts.clear();
    }

    public static synchronized void updateCurrentUser(UserAccount user) throws SQLException {
        currentUser = user;
        getAccountsList();
        HomeController.getInstance().addAccounts();
    }

    public static Account createAccount(String accountName) throws SQLException {
        if (!doesAccountExist(accountName)){
            return new AccountModel(accountName);
        } else {
            throw new IllegalArgumentException("Account already exists.");
        }

    }

    public static boolean doesAccountExist(String accountName) throws SQLException {
        getAccountsList();
        for (Account account : accounts) {
            if (account.getAccountName().equals(accountName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof Account) {
            Account account = (Account) obj;
            return getAccountID() == account.getAccountID();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getAccountID();
    }
}
