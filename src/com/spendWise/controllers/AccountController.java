package com.spendWise.controllers;

import java.net.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

import com.spendWise.models.Account;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;

public class AccountController implements Initializable {

    private StringProperty accountTitle = new SimpleStringProperty();
    private Account account;

    @FXML
    private Label AccountTitleLabel;
    @FXML
    private TextField SelectMonthTextField;
    @FXML
    private Button SelectMonthButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AccountTitleLabel.textProperty().bind(accountTitle);
        AccountTitleLabel.textProperty().addListener(event -> {
            AccountTitleLabel.setAlignment(Pos.CENTER);
            AccountTitleLabel.setTextAlignment(TextAlignment.CENTER);
        });

        SelectMonthButton.setOnAction(event -> {
            String month = SelectMonthTextField.getText();
            if (month.matches("^\\d{4}-(0[1-9]|1[0-2])$")) {
                // TODO: Implement the logic to select the month
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Invalid month format. Please enter in the format YYYY-MM");
                alert.showAndWait();
            }
        });
    }

    public void setAccount(Account account) {
        LocalDate today = LocalDate.now();
        StringBuilder title = new StringBuilder();
        if (account.getAccountName().toLowerCase().contains("account")) {
            title.append(account.getAccountName());
        } else {
            title.append(account.getAccountName()).append(" Account");
        }
        title.append(" for the Month of ").append(today.getMonth().toString().substring(0, 1).toUpperCase())
                .append(today.getMonth().toString().substring(1).toLowerCase()).append(" ").append(today.getYear());

        accountTitle.setValue(title.toString());
        this.account = account;
    }

}
