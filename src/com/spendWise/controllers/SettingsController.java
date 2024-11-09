package com.spendWise.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.spendWise.models.UserAccount;

public class SettingsController implements Initializable {
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField newDisplayNameField;
    @FXML
    private PasswordField CurrentPassword;
    @FXML
    private Button confirmButton;
    @FXML
    private Button changeNameButton;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        confirmButton.setOnAction(event -> {
            String password = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Empty Fields");
                alert.setContentText("Please fill all the fields.");
                alert.showAndWait();
            }else if (!password.equals(confirmPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Password Mismatch");
                alert.setContentText("Passwords do not match.");
                alert.showAndWait();
            }else{
                try {
                    UserAccount.changePassword(password);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Password Changed");
                    alert.setContentText("Password has been changed successfully.");
                    alert.showAndWait();
                    UserAccount.logout();
                    ScreenController.activate("Signin");
                    ScreenController.getStage().setTitle("Sign in");
                    ScreenController.centerStage();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Password Change Failed");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }


            }

    });

    changeNameButton.setOnAction(event -> {
        String displayName = newDisplayNameField.getText();
        if (displayName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Empty Field");
            alert.setContentText("Please fill the field.");
            alert.showAndWait();
        }else{
            try {
                   UserAccount.changeDisplayName(displayName);
                   Alert alert = new Alert(Alert.AlertType.INFORMATION);
                   alert.setTitle("Success");
                   alert.setHeaderText("Display name Changed");
                   alert.setContentText("Display name has been changed successfully.");
                   alert.showAndWait();
                }catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Display name Change Failed");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            
                }
        }
    });

    deleteButton.setOnAction(event -> {
        String password = CurrentPassword.getText();
        if(password.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Empty Field");
            alert.setContentText("Please fill the field.");
            alert.showAndWait();
        }else{
            try {
            UserAccount.deleteUser();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Account Deleted");
            alert.setContentText("Account has been deleted successfully.");
            alert.showAndWait();
            UserAccount.logout();
            ScreenController.activate("Signup");
            ScreenController.getStage().setTitle("Sign up");
            ScreenController.centerStage();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Account Deletion Failed");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    });
}
}


