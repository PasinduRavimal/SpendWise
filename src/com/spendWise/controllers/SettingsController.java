package com.spendWise.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.spendWise.models.UserAccount;

public class SettingsController implements Initializable {
    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private TextField newDisplayNameField;
    @FXML
    private PasswordField ExistingPassword;
    @FXML
    private Button confirmButton;
    @FXML
    private Button changeNameButton;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        confirmButton.setOnAction(event -> {
            String password = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();

            if (password.isEmpty() || newPassword.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Empty Fields");
                alert.setContentText("Please fill all the fields.");
                alert.showAndWait();
            } else {
                try {
                    if (!UserAccount.getCurrentUser().validateCurrentPassword(password)) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Invalid Current Password");
                        alert.setContentText("The current password you entered is incorrect.");
                        alert.showAndWait();
                        return;
                    }
                    UserAccount.getCurrentUser().changePassword(newPassword);
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
                } finally {
                    currentPasswordField.clear();
                    newPasswordField.clear();
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
            } else {
                try {
                    UserAccount.getCurrentUser().changeDisplayName(displayName);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Display name Changed");
                    alert.setContentText("Display name has been changed successfully.");
                    alert.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Display name Change Failed");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                } finally {
                    newDisplayNameField.clear();
                }
            }
        });

        deleteButton.setOnAction(event -> {
            String password = ExistingPassword.getText();
            if (password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Empty Field");
                alert.setContentText("Please fill the field.");
                alert.showAndWait();
            } else {
                Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete your account?");
                if (alertConfirm.showAndWait().get() == ButtonType.OK) {
                    try {
                        UserAccount.getCurrentUser().deleteUserAccount();
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
                    } finally {
                        ExistingPassword.clear();
                    }
                }
            }
        });
    }
}
