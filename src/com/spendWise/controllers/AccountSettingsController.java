package com.spendWise.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.spendWise.models.AccountModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AccountSettingsController implements Initializable {

    @FXML
    private TextField accountNameField;

    @FXML
    private Button createAccountButton;

    @FXML
    private Button renameAccountButton;

    @FXML
    private Button deleteAccountButton;

    private AccountModel model = new AccountModel();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createAccountButton.setOnAction(event -> createAccount());
        renameAccountButton.setOnAction(event -> renameAccount());
        deleteAccountButton.setOnAction(event -> deleteAccount());
    }

    private void createAccount() {
        String accountName = accountNameField.getText();
        if (accountName.isEmpty()) {
            System.out.println("Account name cannot be empty!");
            return;
        }
        try {
            model.createAccount(accountName);
            clearFields();
            System.out.println("Account created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void renameAccount() {
        String oldAccountName = accountNameField.getText();
        String newAccountName = "EnterNewName";

        if (oldAccountName.isEmpty() || newAccountName.isEmpty()) {
            System.out.println("Both old and new account names must be provided!");
            return;
        }

        try {
            model.updateAccount(oldAccountName, newAccountName);
            clearFields();
            System.out.println("Account renamed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteAccount() {
        String accountName = accountNameField.getText();
        if (accountName.isEmpty()) {
            System.out.println("Account name cannot be empty!");
            return;
        }

        try {
            model.deleteAccount(accountName);
            clearFields();
            System.out.println("Account deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        accountNameField.clear();
    }
}
