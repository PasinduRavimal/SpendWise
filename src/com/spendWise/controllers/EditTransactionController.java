package com.spendWise.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.spendWise.models.Account;
import com.spendWise.util.StringAccountConverter;

import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

public class EditTransactionController implements Initializable {

    @FXML
    private Button ButtonSearchTransactions;
    @FXML
    private ComboBox<Account> creditAccountComboBox;
    @FXML
    private ComboBox<Account> debitAccountComboBox;
    @FXML
    private DatePicker DatePickerSelectTransactionDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            creditAccountComboBox.getItems().addAll(Account.getAccountsList());
            debitAccountComboBox.getItems().addAll(Account.getAccountsList());
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }

        creditAccountComboBox.setConverter(new StringAccountConverter());
        debitAccountComboBox.setConverter(new StringAccountConverter());

        ButtonSearchTransactions.setOnAction(event -> {
            try {
                if (creditAccountComboBox.getValue() == null || debitAccountComboBox.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please select both accounts");
                    alert.showAndWait();
                    return;
                } else if (DatePickerSelectTransactionDate.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a date");
                    alert.showAndWait();
                    return;
                } else if (creditAccountComboBox.getValue().equals(debitAccountComboBox.getValue())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Credit and Debit accounts cannot be the same");
                    alert.showAndWait();
                    return;
                } else {
                    try {
                        LocalDate parsedDate = LocalDate.parse(DatePickerSelectTransactionDate.getValue().toString());
                        if (parsedDate.isAfter(LocalDate.now())) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Date cannot be in the future");
                            alert.showAndWait();
                            return;
                        }
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid date");
                        alert.showAndWait();
                        return;
                    }
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/SelectTransaction.fxml"));
                SelectTransactionController selectTransactionController = new SelectTransactionController();
                loader.setController(selectTransactionController);
                Parent selectTransactionRoot = loader.load();
                selectTransactionController.setDetails(debitAccountComboBox.getValue(),
                        creditAccountComboBox.getValue(),
                        Timestamp.valueOf(DatePickerSelectTransactionDate.getValue().toString() + " 00:00:00"));

                Scene selectTransactionScene = new Scene(selectTransactionRoot);
                Stage currentStage = (Stage) ButtonSearchTransactions.getScene().getWindow();
                currentStage.hide();
                currentStage.setScene(selectTransactionScene);
                currentStage.setTitle("Select Transaction");
                currentStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        });
    }
}
