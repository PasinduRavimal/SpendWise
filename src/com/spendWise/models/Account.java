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
    public abstract void setAccountName(String accountName) throws SQLException;
    public abstract void deleteAccount() throws SQLException;

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

            while (rs.next()) {
                accounts.add(new AccountModel(rs.getInt("accountID"), rs.getString("accountName")));
            }

            rs.close();
            ps.close();

            return accounts;
        } catch (SQLException e) {
            // TODO: Remove following code
            DatabaseConnection.getStatement()
                    .executeUpdate("ALTER TABLE accounttypes ADD COLUMN accountOwner VARCHAR(50) NOT NULL");

            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    public static synchronized void resetCurrentUser() {
        currentUser = null;
        accounts.clear();
    }

    public static synchronized void updateCurrentUser(UserAccount user) throws SQLException {
        currentUser = user;
        getAccountsList();
        HomeController.getInstance().addAccounts();
    }

    public static Account createAccount(String accountName) throws SQLException {
        if (!doesAccountExist(accountName)) {
            Account newAccount = new AccountModel(accountName);
            accounts.add(newAccount);
            HomeController.getInstance().addAccounts();
            return newAccount;
        } else {
            return null;
        }

    }

    public static Account getAccountByName(String accountName) throws SQLException {
        getAccountsList();
        for (Account account : accounts) {
            if (account.getAccountName().equals(accountName)) {
                return account;
            }
        }

        return null;
    }

    public static boolean deosAccountHasTransactions(String accountName) throws SQLException {
        try {
            Account account = getAccountByName(accountName);
            if (account == null) {
                return false;
            }
            String query = "SELECT * FROM transactions WHERE debitAccountID = ? OR creditAccountID = ? LIMIT 1";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, account.getAccountID(),
                    account.getAccountID());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                rs.close();
                ps.close();
                return true;
            }

            rs.close();
            ps.close();
            return false;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
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

    public static void deleteAccount(String accountName) throws SQLException {
        try {
            Account account = getAccountByName(accountName);
            if (account == null) {
                throw new SQLException("Account does not exist.");
            }

            if (deosAccountHasTransactions(accountName)) {
                throw new SQLException("Account has transactions.");
            }

            account.deleteAccount();

            accounts.remove(account);
            HomeController.getInstance().addAccounts();
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    public static void renameAccount(String oldAccountName, String newAccountName) throws SQLException {
        Account account = getAccountByName(oldAccountName);
        if (account == null) {
            throw new SQLException("Account does not exist.");
        }

        if (doesAccountExist(newAccountName)) {
            throw new SQLException("Cannot rename because an account with the same name already exists.");
        }

        accounts.get(accounts.indexOf(account)).setAccountName(newAccountName);
        HomeController.getInstance().addAccounts();
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
