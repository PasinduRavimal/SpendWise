package com.spendWise.models;

import com.spendWise.util.DatabaseConnection;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JournalEntryModel extends JournalEntry {

    public JournalEntryModel(int id, LocalDate date, String accountCode, String type, double amount) {
        super(id, date, accountCode, type, amount);
    }
     public static List<JournalEntry> fetchJournalEntriesForMonth(String month) throws IOException {
        List<JournalEntry> entries = new ArrayList<>();
        int monthNumber = convertMonthToNumber(month);
                if (monthNumber == -1) {
                    return entries;
                }
                String query = "SELECT transaction_id, date, account_id, type, amount FROM generaljournal WHERE MONTH(date) = ?";
                try {
                    DatabaseConnection.getConnection();
                    PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(query);
                    ps.setInt(1, monthNumber);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        int transactionId = rs.getInt("transaction_id");
                        LocalDate date = rs.getDate("date").toLocalDate();
                        String accountId = rs.getString("account_id");
                        String type = rs.getString("type");
                        double amount = rs.getDouble("amount");
                        entries.add(new JournalEntry(transactionId, date, accountId, type, amount));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return entries;
                
        
            }
        
            private static int convertMonthToNumber(String month){
        switch (month.toLowerCase()) {
            case "january":
                return 1;
            case "february":
                return 2;
            case "march":
                return 3;
            case "april":
                return 4;
            case "may":
                return 5;
            case "june":
                return 6;
            case "july":
                return 7;
            case "august":
                return 8;
            case "september":
                return 9;
            case "october":
                return 10;
            case "november":
                return 11;
            case "december":
                return 12;
            default:
                return -1;
        }
    }
    
}
