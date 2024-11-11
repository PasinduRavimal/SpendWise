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

    public abstract double getAccountBalance() throws SQLException;

    public abstract Transaction getForwardedBalanceOfTheMonth(int month, int year) throws java.sql.SQLException;

    public abstract Transaction getBalanceOfTheMonth(int month, int year) throws java.sql.SQLException;

    public abstract Transaction getBalanceForwarded() throws java.sql.SQLException;

    public abstract List<Transaction> getCreditTransactions(int month, int year) throws java.sql.SQLException;

    public abstract List<Transaction> getDebitTransactions(int month, int year) throws java.sql.SQLException;

    public static synchronized List<Account> getAccountsList() throws SQLException {
        if (currentUser == null) {
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
        return getAccountsList().parallelStream().filter(account -> account.getAccountName().equals(accountName)).findAny()
                .orElse(null);
    }

    public static boolean doesAccountHasTransactions(String accountName) throws SQLException {
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
        return getAccountsList().parallelStream().anyMatch(account -> account.getAccountName().equals(accountName));
    }

    public static void deleteAccount(String accountName) throws SQLException {
        try {
            Account account = getAccountByName(accountName);
            if (account == null) {
                throw new SQLException("Account does not exist.");
            }

            if (doesAccountHasTransactions(accountName)) {
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

    public static double getCashBookBalance() throws SQLException {
        if (currentUser == null) {
            throw new IllegalStateException("User not logged in.");
        }

        getAccountsList();

        List<Account> cbaccounts = new ArrayList<>();

        accounts.parallelStream().filter(account -> account.getAccountName().toLowerCase().contains("cashbook")
                || account.getAccountName().toLowerCase().contains("cash book")).forEach(cbaccounts::add);

        if (cbaccounts.size() > 1) {
            throw new SQLWarning("Multiple cash books found. Cash book balance is the sum of all cash books.");
        }

        double balance = 0;

        for (Account account : cbaccounts) {
            try {
                String query = "SELECT * FROM currentbalances WHERE accountID = ?";
                PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, account.getAccountID());
                var rs = ps.executeQuery();

                if (rs.next()) {
                    double creditbalance = rs.getDouble("cumulativeCreditSum");
                    double debitbalance = rs.getDouble("cumulativeDebitSum");
                    balance = balance + debitbalance - creditbalance;
                }

                rs.close();
                ps.close();
            } catch (SQLException e) {
                for (Throwable t : e) {
                    if (t instanceof SQLNonTransientConnectionException) {
                        throw new SQLException("Database connection error.");
                    }
                }

                throw e;
            }
        }

        return balance;
    }

    public static double getBankBalance() throws SQLException {
        if (currentUser == null) {
            throw new IllegalStateException("User not logged in.");
        }

        getAccountsList();

        List<Account> bbaccounts = new ArrayList<>();

        accounts.parallelStream().filter(account -> account.getAccountName().toLowerCase().contains("bankaccount")
                || account.getAccountName().toLowerCase().contains("bank account")).forEach(bbaccounts::add);

        if (bbaccounts.size() > 1) {
            throw new SQLWarning("Multiple bank accounts found. Bank balance is the sum of all bank accounts.");
        }
        
        double balance = 0;

        for (Account account : bbaccounts) {

            try {
                String query = "SELECT * FROM currentbalances WHERE accountID = ?";
                PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, account.getAccountID());
                var rs = ps.executeQuery();

                if (rs.next()) {
                    double creditbalance = rs.getDouble("cumulativeCreditSum");
                    double debitbalance = rs.getDouble("cumulativeDebitSum");
                    balance = balance + debitbalance - creditbalance;
                }

                rs.close();
                ps.close();

            } catch (SQLException e) {
                for (Throwable t : e) {
                    if (t instanceof SQLNonTransientConnectionException) {
                        throw new SQLException("Database connection error.");
                    }
                }

                throw e;
            }
        }

        return balance;
    }

    public static Account getAccountByID(int accountID){
        return accounts.parallelStream().filter(account -> account.getAccountID() == accountID).findAny().orElse(null);
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
        return this.getAccountID() +
        this.getAccountName().hashCode();
    }
}
