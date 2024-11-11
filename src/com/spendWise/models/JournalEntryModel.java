package com.spendWise.models;

import com.spendWise.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.List;

public class JournalEntryModel extends JournalEntry {

    public JournalEntryModel(int id, Timestamp date, int debitAccountID, int creditAccountID, String description, double amount) {
        super(id, date, debitAccountID, creditAccountID, description, amount);
    }

    public static List<JournalEntry> fetchJournalEntriesForMonth(LocalDate month) throws IOException, SQLException {
        List<JournalEntry> entries = new ArrayList<>();
        if (month == null) {
            throw new IllegalArgumentException("Month cannot be null");
        }
        String query = "SELECT * FROM generaljournal WHERE MONTH(journalEntryTime) = ? AND YEAR(journalEntryTime) = ?";
        try {
            DatabaseConnection.getConnection();
            PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, month.getMonthValue(), month.getYear());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int journalEntryID = rs.getInt("journalEntryID");
                Timestamp date = rs.getTimestamp("journalEntryTime");
                int creditAccountID = rs.getInt("creditAccountID");
                int debitAccountID = rs.getInt("debitAccountID");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                entries.add(new JournalEntry(journalEntryID, date, debitAccountID, creditAccountID, description, amount));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            for (Throwable t : e) {
                if (t instanceof SQLNonTransientConnectionException) {
                    throw new SQLException("Database connection error", e);
                }
            }

            throw e;
        }
        return entries;

    }

}
