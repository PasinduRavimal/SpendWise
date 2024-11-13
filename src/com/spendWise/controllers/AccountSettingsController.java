package com.spendWise.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.spendWise.models.Account;

import javafx.fxml.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AccountSettingsController implements Initializable {

    @FXML
    private TextField mainAccountNameTextField;
    @FXML
    private TextField oldAccountNameTextField;
    @FXML
    private TextField newAccountNameTextField;
    @FXML
    private Button addAccountButton;
    @FXML
    private Button deleteAccountButton;
    @FXML
    private Button renameAccountButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addAccountButton.setOnAction(event -> {
            try {
                Account newAccount = Account.createAccount(mainAccountNameTextField.getText());
                if (newAccount == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Account already exists.");
                    alert.showAndWait();
                } else {
                    HomeController.getInstance().addAccounts();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Account created successfully.");
                    alert.showAndWait();
                }
                mainAccountNameTextField.clear();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        });

        deleteAccountButton.setOnAction(event -> {
            try {
                if (!Account.doesAccountExist(mainAccountNameTextField.getText())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Account does not exist.");
                    alert.showAndWait();
                } else if (Account.doesAccountHasTransactions(mainAccountNameTextField.getText())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Account has transactions.");
                    alert.showAndWait();
                } else if (Account.doesAccountHasGeneralJournalEntries(mainAccountNameTextField.getText())){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Account has general journal entries.");
                    alert.showAndWait();
                } else {
                    Account.deleteAccount(mainAccountNameTextField.getText());
                    HomeController.getInstance().addAccounts();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Account deleted successfully.");
                    alert.showAndWait();
                    mainAccountNameTextField.clear();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        });

        renameAccountButton.setOnAction(event -> {
            try {
                if (oldAccountNameTextField.getText().isEmpty() || newAccountNameTextField.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill all fields.");
                    alert.showAndWait();
                    return;
                } else if (oldAccountNameTextField.getText().equals(newAccountNameTextField.getText())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Old and new account names are the same.");
                    alert.showAndWait();
                    return;
                } else if (Account.doesAccountExist(newAccountNameTextField.getText())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Cannot rename because an account with the same name already exists.");
                    alert.showAndWait();
                    return;
                } else if (!Account.doesAccountExist(oldAccountNameTextField.getText())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Account does not exist.");
                    alert.showAndWait();
                    return;
                } else {
                    Account.renameAccount(oldAccountNameTextField.getText(), newAccountNameTextField.getText());
                    HomeController.getInstance().addAccounts();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Account renamed successfully.");
                    alert.showAndWait();
                    oldAccountNameTextField.clear();
                    newAccountNameTextField.clear();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        });
    }

}
