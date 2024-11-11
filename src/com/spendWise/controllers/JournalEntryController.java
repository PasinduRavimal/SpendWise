package com.spendWise.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.spendWise.models.JournalEntry;
import com.spendWise.models.JournalEntryModel;

public class JournalEntryController {

    @FXML
    private TextField monthTextField;

    @FXML
    private TableView<JournalEntry> journalTable;

    @FXML
    private TableColumn<JournalEntry, Timestamp> dateColumn;

    @FXML
    private TableColumn<JournalEntry, String> debitColumn;

    @FXML
    private TableColumn<JournalEntry, String> creditColumn;

    @FXML
    private TableColumn<JournalEntry, String> descriptionColumn;

    @FXML
    private TableColumn<JournalEntry, Double> amountColumn;

    private final ObservableList<JournalEntry> journalEntries = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        debitColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().debitAccountProperty().getValue().getAccountName()));
        creditColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().creditAccountProperty().getValue().getAccountName()));
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        journalTable.setItems(journalEntries);
    }

    @FXML
    private void handleShowButtonClick() {
        if (monthTextField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a month");
            alert.showAndWait();
            return;
        }
        try {
            LocalDate month = LocalDate.parse(monthTextField.getText() + "-01");
            List<JournalEntry> entries = JournalEntryModel.fetchJournalEntriesForMonth(month);
            journalEntries.setAll(entries);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        } catch (DateTimeParseException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid month");
            alert.showAndWait();
        }
    }

}
