package com.spendWise.controllers;

import java.net.*;
import java.util.*;

import com.spendWise.models.UserAccount;

import javafx.fxml.*;
import javafx.scene.control.*;

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
                if (UserAccount.signup(username, displayName, password)) {
                    try {
                        ScreenController.activate("Signin");
                        ScreenController.stage.setTitle("Sign in");
                        ScreenController.centerStage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't sign up");
                    alert.showAndWait();
                }
            }
        });
    }

}
