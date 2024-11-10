package com.spendWise.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.spendWise.models.JournalEntry;

public class JournalEntryController  {

    @FXML
    private TextField monthTextField;

    @FXML
    private TableView<JournalEntry> journalTable;

    @FXML
    private TableColumn<JournalEntry, Integer> transactionIdColumn;

    @FXML
    private TableColumn<JournalEntry, LocalDate> dateColumn;

    @FXML
    private TableColumn<JournalEntry, String> accountIdColumn;

    @FXML
    private TableColumn<JournalEntry, String> typeColumn;

    @FXML
    private TableColumn<JournalEntry, Double> amountColumn;

    private final ObservableList<JournalEntry> journalEntries = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        transactionIdColumn.setCellValueFactory(cellData -> cellData.getValue().transactionIdProperty().asObject());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        accountIdColumn.setCellValueFactory(cellData -> cellData.getValue().accountIdProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        journalTable.setItems(journalEntries);
    }

    @FXML
    private void handleShowButtonClick() {
        String month = monthTextField.getText().trim();
        List<JournalEntry> entries = fetchJournalEntriesForMonth(month);
        journalEntries.setAll(entries);
    }

    private List<JournalEntry> fetchJournalEntriesForMonth(String month) {
        return Arrays.asList(
            new JournalEntry(1, LocalDate.of(2024, 1, 10), "A001", "Debit", 100.0),
            new JournalEntry(2, LocalDate.of(2024, 1, 15), "A002", "Credit", 150.0),
            new JournalEntry(3, LocalDate.of(2024, 1, 20), "A003", "Debit", 200.0)
        );
    }
}
