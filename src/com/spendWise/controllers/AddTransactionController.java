package com.spendWise.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;

public class AddTransactionController {

    @FXML
    private ChoiceBox<String> ChoiceBoxSelectDebetAccount;

    @FXML
    private ChoiceBox<String> ChoiceBoxSelectCreditAccount;

    @FXML
    private DatePicker DatePickerSelectTransactionDate;

    @FXML
    private Button ButtonAddTransaction;

    @FXML
    public void initialize() {
        ChoiceBoxSelectDebetAccount.getItems().addAll("Cash Account", "Bank Account");

        ChoiceBoxSelectCreditAccount.getItems().addAll("Electricity Account", "Water Account", "Transport Account");

        ButtonAddTransaction.setOnAction(event -> addTransaction());
    }

    private void addTransaction() {
        

        
    }
}
