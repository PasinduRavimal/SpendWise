package com.spendWise.controllers;

import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

import com.spendWise.models.UserAccount;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.shape.Rectangle;


public class DashboardContentController implements Initializable {

    @FXML 
    private Label LabelCashBalance, LabelBankBalance, LabelTotalBalance;

    @FXML
    private Button ButtonAddTransaction, ButtonCorrectError, ButtonUndo;

    @FXML
    private Rectangle RectangleCashBalance, RectangleBankBalance, RectangleTotalBalance;

    @FXML
    private TabPane TabPaneAccountDetails, TabPaneTransactionHistory;

    @FXML
    private TextField TextDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
}
 