package com.spendWise.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import com.spendWise.models.Account;
import com.spendWise.models.JournalEntryModel;
import com.spendWise.models.Transaction;

public class SelectTransactionController implements Initializable {

    private ObservableList<Transaction> transactions;
    private StringBuilder journalEntryDesc = new StringBuilder();

    @FXML private TableView<Transaction> TableViewSelectTransactions;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, String> amountColumn;
    @FXML private Button ButtonEditTransaction, ButtonDeleteTransaction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        transactions = FXCollections.observableArrayList();
        TableViewSelectTransactions.setItems(transactions);

        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        amountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAmount())));

        ButtonEditTransaction.setOnAction(event -> {
            Transaction selectedTransaction = TableViewSelectTransactions.getSelectionModel().getSelectedItem();

            if (selectedTransaction == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a transaction to edit");
                alert.showAndWait();
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/EditTransactionFinalStage.fxml"));
                EditTransactionFinalStageController controller = new EditTransactionFinalStageController();
                controller.setTransaction(selectedTransaction);
                loader.setController(controller);
                Parent root = loader.load();
                Stage stage = (Stage) ButtonEditTransaction.getScene().getWindow();
                stage.hide();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading edit transaction window");
                alert.showAndWait();
            }
        });

        ButtonDeleteTransaction.setOnAction(event -> {
            Transaction selectedTransaction = TableViewSelectTransactions.getSelectionModel().getSelectedItem();

            if (selectedTransaction == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a transaction to delete");
                alert.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this transaction?");
            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    Transaction transaction = selectedTransaction;
                    selectedTransaction.deleteTransaction();
                    transactions.remove(selectedTransaction);
                    journalEntryDesc.append("{")
                    .append(Account.getAccountByID(transaction.getCreditingAccountID()).getAccountName())
                    .append(",").append(Account.getAccountByID(transaction.getDebitingAccountID()).getAccountName())
                    .append(",")
                    .append(transaction.getAmount()).append(",").append(transaction.getTransactionTime()).append(",")
                    .append(transaction.getDescription()).append("}").append(" deleted.");
                    JournalEntryModel journalEntry = new JournalEntryModel(transaction.getTransactionID(),
                            transaction.getTransactionTime(), transaction.getDebitingAccountID(),
                            transaction.getCreditingAccountID(), journalEntryDesc.toString(), transaction.getAmount());
                    journalEntry.saveToDatabase();
                    selectedTransaction = null;
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error deleting transaction");
                    errorAlert.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error deleting transaction");
                    errorAlert.showAndWait();
                }
            }
        });

    }

    public void setDetails(Account debitAccount, Account creditAccount, Timestamp date) throws SQLException, IOException{
        List<Transaction> transactions = Transaction.getTransactionsByAccountsAndDate(debitAccount, creditAccount, date);
        if (transactions != null) {
            this.transactions.addAll(transactions);
        }
    }

}
