package com.spendWise.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EditTransactionController implements Initializable {

        @FXML private Button ButtonSearchTransactions;
    
        @Override
        public void initialize(URL location, ResourceBundle resources) {
            ButtonSearchTransactions.setOnAction(event -> {
                try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/SelectTransaction.fxml"));
                Parent selectTransactionRoot = loader.load();
                Scene selectTransactionScene = new Scene(selectTransactionRoot);
                Stage currentStage = (Stage) ButtonSearchTransactions.getScene().getWindow();
                currentStage.hide();
                currentStage.setScene(selectTransactionScene);
                currentStage.setTitle("Select Transaction");
                currentStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
            });
        }
}
