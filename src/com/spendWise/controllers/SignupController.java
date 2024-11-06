package com.spendWise.controllers;

import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

import com.spendWise.models.UserAccount;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class SignupController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button signupButton;
    @FXML
    private CheckBox tandcCheckBox;
    @FXML
    private Hyperlink loginLink;
    @FXML
    private TextField displayNameField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signupButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String displayName = displayNameField.getText();
            boolean tandc = tandcCheckBox.isSelected();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || displayName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Empty Fields");
                alert.setContentText("Please fill all the fields.");
                alert.showAndWait();
            } else if (!password.equals(confirmPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Password Mismatch");
                alert.setContentText("Passwords do not match.");
                alert.showAndWait();
            } else if (!tandc) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Terms and Conditions");
                alert.setContentText("Please accept the terms and conditions.");
                alert.showAndWait();
            } else {
                try {
                    if (UserAccount.signup(username, displayName, password)) {
                        UserAccount.logout();
                        ScreenController.activate("Signin");
                        ScreenController.stage.setTitle("Sign in");
                        ScreenController.centerStage();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't sign up");
                        alert.showAndWait();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                } finally {
                    usernameField.clear();
                    passwordField.clear();
                    confirmPasswordField.clear();
                    displayNameField.clear();
                    tandcCheckBox.setSelected(false);
                }

            }
        });

        loginLink.setOnAction(event -> {
            ScreenController.activate("Signin");
            ScreenController.stage.setTitle("Sign in");
            ScreenController.centerStage();
        });
    }

}
