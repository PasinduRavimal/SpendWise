package com.spendWise.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardContentController  implements Initializable {

    @FXML
    private Button ButtonAddTransaction, ButtonCorrectError;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        ButtonAddTransaction.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/addTransaction.fxml"));
                Parent addTransactionRoot = loader.load();
                Scene addTransactionScene = new Scene(addTransactionRoot);
                Stage currentStage = (Stage) ButtonAddTransaction.getScene().getWindow();
                currentStage.setScene(addTransactionScene);
                currentStage.setTitle("Add Transaction");
                currentStage.centerOnScreen(); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ButtonCorrectError.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/correctTransactionError.fxml"));
                Parent correctErrorRoot = loader.load();
                Scene correctErrorScene = new Scene(correctErrorRoot);
                Stage currentStage = (Stage) ButtonCorrectError.getScene().getWindow();
                currentStage.setScene(correctErrorScene);
                currentStage.setTitle("Correct Transaction Error");
                currentStage.centerOnScreen(); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    
}
 