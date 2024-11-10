package com.spendWise.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class JournalEntry {
    private final IntegerProperty transactionId;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty accountId;
    private final StringProperty type;
    private final DoubleProperty amount;

    public JournalEntry(int transactionId, LocalDate date, String accountId, String type, double amount) {
        this.transactionId = new SimpleIntegerProperty(transactionId);
        this.date = new SimpleObjectProperty<>(date);
        this.accountId = new SimpleStringProperty(accountId);
        this.type = new SimpleStringProperty(type);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public IntegerProperty transactionIdProperty() { return transactionId; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty accountIdProperty() { return accountId; }
    public StringProperty typeProperty() { return type; }
    public DoubleProperty amountProperty() { return amount; }
}
