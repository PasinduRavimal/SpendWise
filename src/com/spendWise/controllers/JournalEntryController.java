package com.spendWise.controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.spendWise.models.JournalEntry;
import com.spendWise.models.JournalEntryModel;

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
    private void handleShowButtonClick() throws IOException {
        String month = monthTextField.getText().trim();
        List<JournalEntry> entries = JournalEntryModel.fetchJournalEntriesForMonth(month);
        journalEntries.setAll(entries);
    }

    
}
