package com.spendWise.controllers;

import java.net.*;
import java.util.*;

import javafx.fxml.*;
import javafx.scene.control.*;

public class SignupController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button signupButton;
    @FXML private CheckBox tandcCheckBox;
    @FXML private Hyperlink loginLink;
    @FXML private TextField displayNameField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signupButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Signup Successful!");
            alert.showAndWait();
        });
    }
    
}
