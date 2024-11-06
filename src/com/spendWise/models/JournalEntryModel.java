package com.spendWise.models;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

import com.spendWise.util.DatabaseConnection;

public class JournalEntryModel {

    private int entryID;
    private int transactionID;
    private String username;
    private LocalDateTime entryDate;
    private String description;
    private int accountID;
    private double amount;
    private boolean isDebit; // true for debit, false for credit

    // Constructor without entryID for new entries
    public JournalEntryModel(int transactionID, String username, String description, int accountID, double amount, boolean isDebit) {
        this.transactionID = transactionID;
        this.username = username;
        this.entryDate = LocalDateTime.now(); // Automatically set to current time
        this.description = description;
        this.accountID = accountID;
        this.amount = amount;
        this.isDebit = isDebit;
    }

    // Getters
    public int getEntryID() {
        return entryID;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public String getDescription() {
        return description;
    }

    public int getAccountID() {
        return accountID;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isDebit() {
        return isDebit;
    }

    // Method to save journal entry to database
    public void saveEntry() throws SQLException, IOException {
        String query = "INSERT INTO journal_entries (transactionID, username, entryDate, description, accountID, amount, isDebit) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transactionID);
            stmt.setString(2, username);
            stmt.setTimestamp(3, Timestamp.valueOf(entryDate));
            stmt.setString(4, description);
            stmt.setInt(5, accountID);
            stmt.setDouble(6, amount);
            stmt.setBoolean(7, isDebit);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.entryID = generatedKeys.getInt(1);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "JournalEntryModel{"
                + "entryID=" + entryID
                + ", transactionID=" + transactionID
                + ", username='" + username + '\''
                + ", entryDate=" + entryDate
                + ", description='" + description + '\''
                + ", accountID=" + accountID
                + ", amount=" + amount
                + ", isDebit=" + isDebit
                + '}';
    }

    // Method to create corresponding debit and credit entries for a transaction
    public static void createJournalEntries(TransactionModel transaction) throws SQLException,IOException {
        String username = transaction.getUsername();
        int transactionID = transaction.getTransactionID();
        double amount = transaction.getAmount();
        String description = transaction.getDescription();

        // Debit Entry
        JournalEntryModel debitEntry = new JournalEntryModel(transactionID, username, description,
                transaction.getDebitingAccountID(), amount, true);
        debitEntry.saveEntry();

        // Credit Entry
        JournalEntryModel creditEntry = new JournalEntryModel(transactionID, username, description,
                transaction.getCreditingAccountID(), amount, false);
        creditEntry.saveEntry();
    }
}
