package com.spendWise.controllers;

import java.net.URL;
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
            try{
            Account.createAccount(mainAccountNameTextField.getText());
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        });
    }

}
