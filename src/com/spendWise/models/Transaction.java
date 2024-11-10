package com.spendWise.models;

import java.util.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public static Transaction getForwardedBalanceOfTheMonth(Account account, int month, int Year)
            throws SQLException {
        try {
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactionssummary WHERE accountID = ? AND transactionMonth < ? ORDER BY accountID, transactionMonth DESC LIMIT 1;";
            PreparedStatement ps;
            Date date = Date.valueOf(LocalDate.of(Year, month, 1));
            ps = DatabaseConnection.getPreparedStatement(query, account.getAccountID(), date);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double creditAmount = rs.getDouble("creditSum");
                double debitAmount = rs.getDouble("debitSum");
                String out = date.toLocalDate().toString();

                if (creditAmount > debitAmount) {
                    return new TransactionModel(username, -1, account.getAccountID(),
                            Timestamp.valueOf(out + " 00:00:00"), creditAmount - debitAmount,
                            "Balance Brought Forward");
                } else if (debitAmount > creditAmount) {
                    return new TransactionModel(username, account.getAccountID(), -1,
                            Timestamp.valueOf(out + " 00:00:00"), debitAmount - creditAmount,
                            "Balance Brought Forward");
                }
            }
            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Transaction getBalanceOfTheMonth(Account account, int month, int Year)
            throws SQLException {
        try {
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactionssummary WHERE accountID = ? AND MONTH(transactionMonth) = ? AND YEAR(transactionMonth) = ?";
            PreparedStatement ps;
            ps = DatabaseConnection.getPreparedStatement(query, account.getAccountID(), month, Year);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double creditAmount = rs.getDouble("creditSum");
                double debitAmount = rs.getDouble("debitSum");
                String out = rs.getDate("transactionMonth").toString();
                out = LocalDate.parse(out, DateTimeFormatter.ISO_LOCAL_DATE).plusMonths(1).toString();

                if (creditAmount > debitAmount) {
                    return new TransactionModel(username, -1, account.getAccountID(),
                            Timestamp.valueOf(out + " 00:00:00"), creditAmount - debitAmount,
                            "Balance Brought Forward");
                } else if (debitAmount > creditAmount) {
                    return new TransactionModel(username, account.getAccountID(), -1,
                            Timestamp.valueOf(out + " 00:00:00"), debitAmount - creditAmount,
                            "Balance Brought Forward");
                }
            }
            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Transaction getBalanceForwarded(Account account) throws SQLException {
        try {
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM forwardedbalances WHERE accountID = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, account.getAccountID());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double creditAmount = rs.getDouble("creditSum");
                double debitAmount = rs.getDouble("debitSum");
                String out = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1).toString();

                if (creditAmount > debitAmount) {
                    return new TransactionModel(username, -1, account.getAccountID(),
                            Timestamp.valueOf(out + " 00:00:00"), creditAmount - debitAmount,
                            "Balance Brought Forward");
                } else if (debitAmount > creditAmount) {
                    return new TransactionModel(username, account.getAccountID(), -1,
                            Timestamp.valueOf(out + " 00:00:00"), debitAmount - creditAmount,
                            "Balance Brought Forward");
                }
            }
            rs.close();
            ps.close();
            return null;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static List<Transaction> getCreditTransactions(Account account, int month, int Year)
            throws SQLException {
        try {
            List<Transaction> transactions = new ArrayList<>();
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactions WHERE username = ? AND creditAccountID = ? AND MONTH(transactionTime) = ? AND YEAR(transactionTime) = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username,
                    account.getAccountID(), month, Year);
            ResultSet rs = ps.executeQuery();

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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Transaction> getDebitTransactions(Account account, int month, int Year)
            throws SQLException {
        try {
            List<Transaction> transactions = new ArrayList<>();
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactions WHERE username = ? AND debitAccountID = ? AND MONTH(transactionTime) = ? AND YEAR(transactionTime) = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username,
                    account.getAccountID(), month, Year);
            ResultSet rs = ps.executeQuery();

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
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
