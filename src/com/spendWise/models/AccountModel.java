package com.spendWise.models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.spendWise.util.DatabaseConnection;

public class AccountModel extends Account {
    private int accountID;
    private String accountName = null;

    public AccountModel(int accountID, String accountName) {
        this.accountID = accountID;
        this.accountName = accountName;
    }

    protected AccountModel(String accountName) throws SQLException {
        this.accountName = accountName;
        addToDatabase();
        this.accountID = getAccountIDByName(accountName);
    }

    public int getAccountID() {
        return accountID;
    }

    public String getAccountName() {
        return accountName;
    }

    void setAccountName(String accountName) throws SQLException {
        try {
            String query = "UPDATE accounttypes SET accountName = ? WHERE accountID = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, accountName, accountID);
            ps.executeUpdate();
            ps.close();
            this.accountName = accountName;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    void deleteAccount() throws SQLException {
        try {
            String query = "DELETE FROM accounttypes WHERE accountID = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, accountID);
            ps.executeUpdate();
            ps.close();
            this.accountName = null;
            this.accountID = -1;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    private int getAccountIDByName(String name) throws SQLException{
        try {
            String query = "SELECT accountID FROM accounttypes WHERE accountName = ? AND accountOwner = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, accountName,
                    UserAccount.getCurrentUser().getUsername());
            ResultSet rs = ps.executeQuery();
            int accountID = -1;
            if (rs.next()) {
                 accountID = rs.getInt("accountID");
            }

            rs.close();
            ps.close();

            return accountID;

        } catch (SQLException e) {
            e.printStackTrace();
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    void addToDatabase() throws SQLException {
        try {
            String query = "INSERT INTO accounttypes (accountName, accountOwner) VALUES (?, ?)";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, accountName,
                    UserAccount.getCurrentUser().getUsername());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    public double getAccountBalance() throws SQLException {
        try {
            String query = "SELECT * FROM currentbalances WHERE accountID = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, accountID);
            var rs = ps.executeQuery();

            double balance = 0;

            if (rs.next()) {
                double creditbalance = rs.getDouble("cumulativeCreditSum");
                double debitbalance = rs.getDouble("cumulativeDebitSum");
                balance = debitbalance - creditbalance;
            }

            rs.close();
            ps.close();

            return balance;
        } catch (SQLException e) {
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error.");
                }
            }

            throw e;
        }
    }

    public Transaction getForwardedBalanceOfTheMonth(int month, int Year)
            throws SQLException {
        try {
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactionssummary WHERE accountID = ? AND transactionMonth < ? ORDER BY accountID, transactionMonth DESC LIMIT 1;";
            PreparedStatement ps;
            Date date = Date.valueOf(LocalDate.of(Year, month, 1));
            ps = DatabaseConnection.getPreparedStatement(query, accountID, date);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double creditAmount = rs.getDouble("creditSum");
                double debitAmount = rs.getDouble("debitSum");
                String out = date.toLocalDate().toString();

                if (creditAmount > debitAmount) {
                    return new TransactionModel(username, -1, accountID,
                            Timestamp.valueOf(out + " 00:00:00"), creditAmount - debitAmount,
                            "Balance Brought Forward");
                } else if (debitAmount > creditAmount) {
                    return new TransactionModel(username, accountID, -1,
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

    public Transaction getBalanceOfTheMonth(int month, int Year)
            throws SQLException {
        try {
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactionssummary WHERE accountID = ? AND MONTH(transactionMonth) = ? AND YEAR(transactionMonth) = ?";
            PreparedStatement ps;
            ps = DatabaseConnection.getPreparedStatement(query, accountID, month, Year);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double creditAmount = rs.getDouble("creditSum");
                double debitAmount = rs.getDouble("debitSum");
                String out = rs.getDate("transactionMonth").toString();
                out = LocalDate.parse(out, DateTimeFormatter.ISO_LOCAL_DATE).plusMonths(1).toString();

                if (creditAmount > debitAmount) {
                    return new TransactionModel(username, -1, accountID,
                            Timestamp.valueOf(out + " 00:00:00"), creditAmount - debitAmount,
                            "Balance Brought Forward");
                } else if (debitAmount > creditAmount) {
                    return new TransactionModel(username, accountID, -1,
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

    public Transaction getBalanceForwarded() throws SQLException {
        try {
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM forwardedbalances WHERE accountID = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, accountID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double creditAmount = rs.getDouble("creditSum");
                double debitAmount = rs.getDouble("debitSum");
                String out = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth() - 1).toString();

                if (creditAmount > debitAmount) {
                    return new TransactionModel(username, -1, accountID,
                            Timestamp.valueOf(out + " 00:00:00"), creditAmount - debitAmount,
                            "Balance Brought Forward");
                } else if (debitAmount > creditAmount) {
                    return new TransactionModel(username, accountID, -1,
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

    public List<Transaction> getCreditTransactions(int month, int Year)
            throws SQLException {
        try {
            List<Transaction> transactions = new ArrayList<>();
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactions WHERE username = ? AND creditAccountID = ? AND MONTH(transactionTime) = ? AND YEAR(transactionTime) = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username, accountID, month, Year);
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

    public List<Transaction> getDebitTransactions(int month, int Year)
            throws SQLException {
        try {
            List<Transaction> transactions = new ArrayList<>();
            String username = UserAccount.getCurrentUser().getUsername();
            String query = "SELECT * FROM transactions WHERE username = ? AND debitAccountID = ? AND MONTH(transactionTime) = ? AND YEAR(transactionTime) = ?";
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, username,
                    accountID, month, Year);
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
