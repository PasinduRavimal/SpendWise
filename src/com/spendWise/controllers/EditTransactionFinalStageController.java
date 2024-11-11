package com.spendWise.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.sql.SQLException;

import com.spendWise.models.Account;
import com.spendWise.models.JournalEntryModel;
import com.spendWise.models.Transaction;
import com.spendWise.util.StringAccountConverter;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class EditTransactionFinalStageController {

    private Transaction transaction;
    private StringBuilder journalEntryDesc = new StringBuilder();

    @FXML
    private ComboBox<Account> creditAccountComboBox;

    @FXML
    private ComboBox<Account> debitAccountComboBox;

    @FXML
    private DatePicker DatePickerSelectTransactionDate;

    @FXML
    private Button ButtonAddTransaction;

    @FXML
    private TextField TextFieldAmount, TextFieldDescription;

    @FXML
    public void initialize() {

        try {
            creditAccountComboBox.getItems().addAll(Account.getAccountsList());
            creditAccountComboBox.setConverter(new StringAccountConverter());

            debitAccountComboBox.getItems().addAll(Account.getAccountsList());
            debitAccountComboBox.setConverter(new StringAccountConverter());

            creditAccountComboBox.getSelectionModel()
                    .select(Account.getAccountByID(transaction.getCreditingAccountID()));
            debitAccountComboBox.getSelectionModel().select(Account.getAccountByID(transaction.getDebitingAccountID()));
            DatePickerSelectTransactionDate
                    .setValue(LocalDate.parse(transaction.getTransactionTime().toString().substring(0, 10)));
            TextFieldAmount.setText(String.valueOf(transaction.getAmount()));
            TextFieldDescription.setText(transaction.getDescription());

            journalEntryDesc.append("{")
                    .append(Account.getAccountByID(transaction.getCreditingAccountID()).getAccountName())
                    .append(",").append(Account.getAccountByID(transaction.getDebitingAccountID()).getAccountName())
                    .append(",")
                    .append(transaction.getAmount()).append(",").append(transaction.getTransactionTime()).append(",")
                    .append(transaction.getDescription()).append("}").append("changed to ");

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading accounts");
            alert.showAndWait();
        }

        ButtonAddTransaction.setOnAction(event -> {
            if (creditAccountComboBox.getValue() == null || debitAccountComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select both accounts");
                alert.showAndWait();
                return;
            } else if (DatePickerSelectTransactionDate.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a date");
                alert.showAndWait();
                return;
            } else if (TextFieldAmount.getText().isEmpty()
                    || !TextFieldAmount.getText().matches("^(\\d+)$|^(\\d*\\.\\d+)$")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter an amount");
                alert.showAndWait();
                return;
            } else if (creditAccountComboBox.getValue().equals(debitAccountComboBox.getValue())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Credit and debit accounts cannot be the same");
                alert.showAndWait();
                return;
            } else if (TextFieldDescription.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a description");
                alert.showAndWait();
                return;
            } else {
                try {
                    LocalDate parsedDate = LocalDate.parse(DatePickerSelectTransactionDate.getValue().toString());
                    if (parsedDate.isAfter(LocalDate.now())) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Date cannot be in the future");
                        alert.showAndWait();
                        return;
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid date");
                    alert.showAndWait();
                    return;
                }

                transaction.setDebitingAccountID(debitAccountComboBox.getValue().getAccountID());
                transaction.setCreditingAccountID(creditAccountComboBox.getValue().getAccountID());
                transaction.setTransactionTime(
                        Timestamp.valueOf(DatePickerSelectTransactionDate.getValue().atStartOfDay()));
                transaction.setAmount(Double.parseDouble(TextFieldAmount.getText()));
                transaction.setDescription(TextFieldDescription.getText());
                try {
                    transaction.updateTransaction();
                    journalEntryDesc.append("{")
                            .append(Account.getAccountByID(transaction.getCreditingAccountID()).getAccountName())
                            .append(",")
                            .append(Account.getAccountByID(transaction.getDebitingAccountID()).getAccountName())
                            .append(",")
                            .append(transaction.getAmount()).append(",").append(transaction.getTransactionTime())
                            .append(",")
                            .append(transaction.getDescription()).append("}");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transaction updated successfully");
                    alert.show();
                    JournalEntryModel journalEntry = new JournalEntryModel(transaction.getTransactionID(),
                            transaction.getTransactionTime(), transaction.getDebitingAccountID(),
                            transaction.getCreditingAccountID(), journalEntryDesc.toString(), transaction.getAmount());
                    journalEntry.saveToDatabase();
                    this.ButtonAddTransaction.getScene().getWindow().hide();
                    creditAccountComboBox.getSelectionModel().clearSelection();
                    debitAccountComboBox.getSelectionModel().clearSelection();
                    DatePickerSelectTransactionDate.getEditor().clear();
                    TextFieldAmount.clear();
                    TextFieldDescription.clear();
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                }
            }
        });
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

}
