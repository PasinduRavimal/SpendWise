package com.spendWise.controllers;

import java.net.*;
import java.security.Timestamp;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

import com.spendWise.models.Account;
import com.spendWise.models.Transaction;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class AccountController implements Initializable {

    private StringProperty accountTitle = new SimpleStringProperty();
    private Account account;
    private ObservableList<Transaction> debitTransactions;
    private ObservableList<Transaction> creditTransactions;

    @FXML
    private Label AccountTitleLabel;
    @FXML
    private TextField SelectMonthTextField;
    @FXML
    private Button SelectMonthButton;
    @FXML
    private TableView<Transaction> debitColumn;
    @FXML
    private TableView<Transaction> creditColumn;
    @FXML
    private TableColumn<Transaction, Timestamp> debitColumnDate;
    @FXML
    private TableColumn<Transaction, String> debitColumnDescription;
    @FXML
    private TableColumn<Transaction, Double> debitColumnAmount;
    @FXML
    private TableColumn<Transaction, Timestamp> creditColumnDate;
    @FXML
    private TableColumn<Transaction, String> creditColumnDescription;
    @FXML
    private TableColumn<Transaction, Double> creditColumnAmount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AccountTitleLabel.textProperty().bind(accountTitle);
        AccountTitleLabel.textProperty().addListener(event -> {
            AccountTitleLabel.setAlignment(Pos.CENTER);
            AccountTitleLabel.setTextAlignment(TextAlignment.CENTER);
        });

        debitColumn.setItems(debitTransactions);
        creditColumn.setItems(creditTransactions);

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

        debitTransactions = FXCollections.observableArrayList();
        creditTransactions = FXCollections.observableArrayList();

        this.account = account;

        loadTransactions();

    }

    public void loadTransactions() {
        ExecutorService executor = Executors.newCachedThreadPool();
        LocalDate today = LocalDate.now();

        Task<ObservableList<Transaction>> debitTask = new Task<ObservableList<Transaction>>() {
            @Override
            protected ObservableList<Transaction> call() throws SQLException {
                List<Transaction> list = Transaction.getDebitTransactions(account, today.getMonthValue(), today.getYear());
                for (Transaction transaction : list) {
                    System.out.println(transaction.getDescription());
                }
                return FXCollections.observableArrayList(list);
            }
        };

        executor.execute(debitTask);

        debitTask.setOnSucceeded(event -> {
            Platform.runLater(() -> debitTransactions.setAll(debitTask.getValue()));
        });

        debitTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load the debit transactions");
                alert.getDialogPane().setExpandableContent(new Text(debitTask.getException().getMessage()));
                alert.showAndWait();
            });
        });

        Task<ObservableList<Transaction>> creditTask = new Task<ObservableList<Transaction>>() {
            @Override
            protected ObservableList<Transaction> call() throws SQLException {
                List<Transaction> list = Transaction.getCreditTransactions(account, today.getMonthValue(), today.getYear());
                for (Transaction transaction : list) {
                    System.out.println(transaction.getDescription());
                }
                return FXCollections.observableArrayList(list);
            }
        };

        executor.execute(creditTask);

        creditTask.setOnSucceeded(event -> {
            Platform.runLater(() -> creditTransactions.setAll(creditTask.getValue()));
        });

        creditTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load the debit transactions");
                alert.getDialogPane().setExpandableContent(new Text(creditTask.getException().getMessage()));
                alert.showAndWait();
            });
        });

        executor.shutdown();
    }

}
