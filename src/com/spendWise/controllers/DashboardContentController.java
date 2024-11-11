package com.spendWise.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.*;

import com.spendWise.models.Account;
import com.spendWise.models.Transaction;

public class DashboardContentController implements Initializable {

    private static StringProperty cashBalance = new SimpleStringProperty("0");
    private static StringProperty bankBalance = new SimpleStringProperty("0");
    private static StringProperty userLabelProperty = new SimpleStringProperty();
    private static StringProperty lastTransaction1Property = new SimpleStringProperty();
    private static StringProperty lastTransaction2Property = new SimpleStringProperty();
    private static StringProperty lastTransaction3Property = new SimpleStringProperty();

    @FXML
    private Button ButtonAddTransaction, ButtonEditTransaction;

    @FXML
    private Label cashBalanceLabel;
    @FXML
    private Label bankBalanceLabel;
    @FXML
    private Label totalBalanceLabel;
    @FXML
    private Label userLabel;
    @FXML private Text lastTransaction1, lastTransaction2, lastTransaction3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cashBalance.addListener((property) -> {
            totalBalanceLabel.setText(String
                    .valueOf(Double.parseDouble(cashBalance.getValue()) + Double.parseDouble(bankBalance.getValue())));
        });

        bankBalance.addListener((property) -> {
            totalBalanceLabel.setText(String
                    .valueOf(Double.parseDouble(cashBalance.getValue()) + Double.parseDouble(bankBalance.getValue())));
        });

        ButtonAddTransaction.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/addTransaction.fxml"));
                Parent addTransactionRoot = loader.load();
                Scene addTransactionScene = new Scene(addTransactionRoot);
                Stage currentStage = new Stage();
                currentStage.initModality(Modality.APPLICATION_MODAL);
                currentStage.setScene(addTransactionScene);
                currentStage.setTitle("Add Transaction");
                currentStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ButtonEditTransaction.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/EditTransaction.fxml"));
                Parent editTransactionRoot = loader.load();
                Scene editTransactionScene = new Scene(editTransactionRoot);
                Stage currentStage = new Stage();
                currentStage.setScene(editTransactionScene);
                currentStage.initModality(Modality.APPLICATION_MODAL);
                currentStage.setTitle("Edit Transaction");
                currentStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        cashBalanceLabel.textProperty().bind(cashBalance);
        bankBalanceLabel.textProperty().bind(bankBalance);
        userLabel.textProperty().bind(userLabelProperty);
        lastTransaction1.textProperty().bind(lastTransaction1Property);
        lastTransaction2.textProperty().bind(lastTransaction2Property);
        lastTransaction3.textProperty().bind(lastTransaction3Property);
    }

    public static void setCashBalance(double cashBalance) {
        DashboardContentController.cashBalance.set(String.valueOf(cashBalance));
    }

    public static void setBankBalance(double bankBalance) {
        DashboardContentController.bankBalance.set(String.valueOf(bankBalance));
    }

    public static void setUserLabel(String userLabel) {
        DashboardContentController.userLabelProperty.set(userLabel);
    }

    public static void updateLastTransactions() {
        try {
            List<Transaction> transactions = Transaction.getLastTransactions(3);
            if (transactions.size() == 0) {
                lastTransaction1Property.setValue("No transactions yet");
                lastTransaction2Property.setValue("No transactions yet");
                lastTransaction3Property.setValue("No transactions yet");
            } else if (transactions.size() == 1){
                lastTransaction1Property.setValue(transactions.get(0).getDescription() + " with amount: " + transactions.get(0).getAmount());
                lastTransaction2Property.setValue("No transactions yet");
                lastTransaction3Property.setValue("No transactions yet");
            } else if (transactions.size() == 2){
                lastTransaction1Property.setValue(transactions.get(0).getDescription() + " with amount: " + transactions.get(0).getAmount());
                lastTransaction2Property.setValue(transactions.get(1).getDescription() + " with amount: " + transactions.get(1).getAmount());
                lastTransaction3Property.setValue("No transactions yet");
            } else {
                lastTransaction1Property.setValue(transactions.get(0).getDescription() + " with amount: " + transactions.get(0).getAmount());
                lastTransaction2Property.setValue(transactions.get(1).getDescription() + " with amount: " + transactions.get(1).getAmount());
                lastTransaction3Property.setValue(transactions.get(2).getDescription() + " with amount: " + transactions.get(2).getAmount());
            }
            setBankBalance(Account.getBankBalance());
            setCashBalance(Account.getCashBookBalance());
        } catch (SQLWarning e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
