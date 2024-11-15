package com.spendWise.controllers;

import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

import com.spendWise.models.UserAccount;

import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

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
                try {
                    UserAccount userAccount = UserAccount.login(username, password);
                    if (userAccount != null) {
                        ScreenController.activate("Home");
                        ScreenController.getStage().setTitle("SpendWise");
                        ScreenController.centerStage();

                        HomeController.getInstance().addAccounts();
                        DashboardContentController.setUserLabel(userAccount.getDisplayName());
                        DashboardContentController.updateLastTransactions();

                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Username or password is incorrect.");
                        alert.showAndWait();
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                    Platform.exit();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                    Platform.exit();
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                    Platform.exit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                    alert.showAndWait();
                    Platform.exit();
                } finally {
                    usernameField.clear();
                    passwordField.clear();
                }
            }

        });
        signUpLink.setOnAction(event -> {
            ScreenController.activate("Signup");
            ScreenController.getStage().setTitle("Sign up");
            ScreenController.centerStage();
        });
    }

}
