package com.spendWise.controllers;

import java.net.*;
import java.util.*;

import com.spendWise.models.UserAccount;

import javafx.fxml.*;
import javafx.scene.control.*;

public class SigninController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signinButton;
    @FXML
    private Hyperlink signUpLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signinButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
        
            if (username.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Empty Fields");
                alert.setContentText("Please fill all the fields.");
                alert.showAndWait();
            } else {
                UserAccount userAccount = UserAccount.login(username, password);
                if (userAccount != null) {
                    try {
                    ScreenController.activate("Signup");
                    ScreenController.stage.setTitle("Sign up");
                    ScreenController.centerStage();
                    } catch (Exception e) {
                    e.printStackTrace();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Username or password is incorrect.");
                    alert.showAndWait();
                }
            }
        
        });
        signUpLink.setOnAction(event -> {
            try {
                ScreenController.activate("Signup");
                ScreenController.stage.setTitle("Sign up");
                ScreenController.centerStage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
