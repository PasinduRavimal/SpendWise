package com.spendWise.controllers;

import java.net.*;
import java.util.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class AccountController implements Initializable {

    private StringProperty accountTitle = new SimpleStringProperty();

    @FXML private Label AccountTitleLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AccountTitleLabel.textProperty().bind(accountTitle);
    }

    public void setAccountTitle(String title){
        accountTitle.setValue(title);
    }
    
}
