package com.spendWise.controllers;

import java.net.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import com.spendWise.models.Account;
import com.spendWise.models.Transaction;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
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
    private ObservableList<Transaction> debitTransactions = FXCollections.observableArrayList();
    private ObservableList<Transaction> creditTransactions = FXCollections.observableArrayList();

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
    private TableColumn<Transaction, String> debitColumnDate;
    @FXML
    private TableColumn<Transaction, String> debitColumnDescription;
    @FXML
    private TableColumn<Transaction, Double> debitColumnAmount;
    @FXML
    private TableColumn<Transaction, String> creditColumnDate;
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

        debitColumnDate.setCellValueFactory(
                celldata -> new SimpleStringProperty(celldata.getValue().getTransactionTime().toString().substring(0, 10)));
        debitColumnDescription
                .setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getDescription()));
        debitColumnAmount
                .setCellValueFactory(celldata -> new SimpleDoubleProperty(celldata.getValue().getAmount()).asObject());

        creditColumnDate.setCellValueFactory(
                celldata -> new SimpleStringProperty(celldata.getValue().getTransactionTime().toString().substring(0, 10)));
        creditColumnDescription
                .setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getDescription()));
        creditColumnAmount
                .setCellValueFactory(celldata -> new SimpleDoubleProperty(celldata.getValue().getAmount()).asObject());

        SelectMonthButton.setOnAction(event -> {
            String month = SelectMonthTextField.getText();
            if (month.matches("^\\d{4}-(0[1-9]|1[0-2])$")) {
                setAccount(LocalDate.parse(month + "-01", DateTimeFormatter.ISO_LOCAL_DATE));
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

        loadTransactions(null);

    }

    public void setAccount(LocalDate date) {
        LocalDate today;
        if (date == null)
            today = LocalDate.now();
        else
            today = date;
        StringBuilder title = new StringBuilder();
        if (account.getAccountName().toLowerCase().contains("account")) {
            title.append(account.getAccountName());
        } else {
            title.append(account.getAccountName()).append(" Account");
        }
        title.append(" for the Month of ").append(today.getMonth().toString().substring(0, 1).toUpperCase())
                .append(today.getMonth().toString().substring(1).toLowerCase()).append(" ").append(today.getYear());

        accountTitle.setValue(title.toString());

        loadTransactions(today);

    }

    public void loadTransactions(LocalDate date) {
        ExecutorService executor = Executors.newCachedThreadPool();
        LocalDate today;
        if (date == null)
            today = LocalDate.now();
        else
            today = date;

        Platform.runLater(() -> {
            debitTransactions.clear();
            creditTransactions.clear();
        });

        try {
            Transaction previousMonthBalance;
            if (today.equals(LocalDate.now())) {
                previousMonthBalance = account.getBalanceForwarded();
            } else {
                previousMonthBalance = account.getForwardedBalanceOfTheMonth(today.getMonthValue(), today.getYear());
            }

            if (previousMonthBalance != null) {
                if (previousMonthBalance.getDebitingAccountID() >= 0) {
                    Platform.runLater(() -> debitTransactions.add(previousMonthBalance));
                } else {
                    Platform.runLater(() -> creditTransactions.add(previousMonthBalance));
                }
            }
        } catch (SQLException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load the previous month balance");
                alert.getDialogPane().setExpandableContent(new Text(e.getMessage()));
                alert.showAndWait();
            });
        }

        Task<ObservableList<Transaction>> debitTask = new Task<ObservableList<Transaction>>() {
            @Override
            protected ObservableList<Transaction> call() throws SQLException {
                List<Transaction> list = account.getDebitTransactions(today.getMonthValue(),
                        today.getYear());
                return FXCollections.observableArrayList(list);
            }
        };

        executor.execute(debitTask);

        debitTask.setOnSucceeded(event -> {
            Platform.runLater(() -> debitTransactions.addAll(debitTask.getValue()));
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
                List<Transaction> list = account.getCreditTransactions(today.getMonthValue(), today.getYear());
                return FXCollections.observableArrayList(list);
            }
        };

        executor.execute(creditTask);

        creditTask.setOnSucceeded(event -> {
            Platform.runLater(() -> creditTransactions.addAll(creditTask.getValue()));
        });

        creditTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't load the credit transactions");
                alert.getDialogPane().setExpandableContent(new Text(creditTask.getException().getMessage()));
                alert.showAndWait();
            });
        });

        executor.shutdown();
    }

}
