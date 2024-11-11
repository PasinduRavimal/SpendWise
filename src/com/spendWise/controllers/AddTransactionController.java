package com.spendWise.controllers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.sql.SQLException;

import com.spendWise.models.Account;
import com.spendWise.models.Transaction;
import com.spendWise.models.UserAccount;
import com.spendWise.util.StringAccountConverter;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class AddTransactionController {

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
            creditAccountComboBox.getItems().setAll(Account.getAccountsList());
            creditAccountComboBox.setConverter(new StringAccountConverter());

            debitAccountComboBox.getItems().setAll(Account.getAccountsList());
            debitAccountComboBox.setConverter(new StringAccountConverter());

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
            } else if (TextFieldAmount.getText().isEmpty() || !TextFieldAmount.getText().matches("^(\\d+)$|^(\\d*\\.\\d+)$")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter an amount");
                alert.showAndWait();
                return;
            } else if (creditAccountComboBox.getValue().equals(debitAccountComboBox.getValue())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Credit and debit accounts cannot be the same");
                alert.showAndWait();
                return;
            } else if (TextFieldDescription.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a description");
                alert.showAndWait();
                return;
            } else {
                try {
                    LocalDate parsedDate = LocalDate.parse(DatePickerSelectTransactionDate.getValue().toString());
                    if (parsedDate.isAfter(LocalDate.now())){
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Date cannot be in the future");
                        alert.showAndWait();
                        return;
                    }
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid date");
                    alert.showAndWait();
                    return;
                }

                Transaction trx = Transaction.getNewTransaction(UserAccount.getCurrentUser().getUsername(), debitAccountComboBox.getValue().getAccountID(),
                creditAccountComboBox.getValue().getAccountID(), Timestamp.valueOf(DatePickerSelectTransactionDate.getValue().toString() + " 00:00:00"), Double.parseDouble(TextFieldAmount.getText()), TextFieldDescription.getText());
                try{
                    trx.saveTransaction();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transaction saved successfully");
                    alert.show();
                    this.ButtonAddTransaction.getScene().getWindow().hide();
                    creditAccountComboBox.getSelectionModel().clearSelection();
                    debitAccountComboBox.getSelectionModel().clearSelection();
                    DatePickerSelectTransactionDate.getEditor().clear();
                    TextFieldAmount.clear();
                    TextFieldDescription.clear();
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error saving transaction");
                    alert.showAndWait();
                }
            }
        });
    }

}
