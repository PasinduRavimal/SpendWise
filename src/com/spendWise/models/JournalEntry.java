package com.spendWise.models;

import javafx.beans.property.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.spendWise.util.DatabaseConnection;

public class JournalEntry {
    private final IntegerProperty journalEntryId;
    private final ObjectProperty<Timestamp> date;
    private final ObjectProperty<Account> creditAccount;
    private final ObjectProperty<Account> debitAccount;
    private final StringProperty description;
    private final DoubleProperty amount;

    public JournalEntry(int journalEntryId, Timestamp date, int debitAccountID, int creditAccountID, String description, double amount) {
        this.journalEntryId = new SimpleIntegerProperty(journalEntryId);
        this.date = new SimpleObjectProperty<Timestamp>(date);
        this.creditAccount = new SimpleObjectProperty<Account>(Account.getAccountByID(creditAccountID));
        this.debitAccount = new SimpleObjectProperty<Account>(Account.getAccountByID(debitAccountID));
        this.description = new SimpleStringProperty(description);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public IntegerProperty journalEntryIdProperty() { return journalEntryId; }
    public ObjectProperty<Timestamp> dateProperty() { return date; }
    public ObjectProperty<Account> creditAccountProperty() { return creditAccount; }
    public ObjectProperty<Account> debitAccountProperty() { return debitAccount; }
    public StringProperty descriptionProperty() { return description; }
    public DoubleProperty amountProperty() { return amount; }

    public void saveToDatabase() throws SQLException, IOException {
        String query = "INSERT INTO generaljournal (username, debitAccountID, creditAccountID, journalEntryTime, amount, description) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = DatabaseConnection.getPreparedStatement(query, UserAccount.getCurrentUser().getUsername(), debitAccount.getValue().getAccountID(), creditAccount.getValue().getAccountID(), date.getValue(), amount.getValue(), description.getValue());
        ps.executeUpdate();
        ps.close();
    }

    
}
