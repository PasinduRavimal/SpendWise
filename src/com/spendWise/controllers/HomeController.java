package com.spendWise.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.spendWise.models.UserAccount;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class HomeController implements Initializable {

    @FXML
    private TitledPane dashboardPane;
    @FXML
    private TitledPane accountsPane;
    @FXML private TitledPane settingsPane;
    @FXML private TitledPane helpPane;
    @FXML private Pane contentPane;
    @FXML private TitledPane logoutPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ContentController contentController = new ContentController();
        contentController.setContent("dashboard");

        dashboardPane.setOnMouseClicked(event -> {
            contentController.setContent("dashboard");
        });
        settingsPane.setOnMouseClicked(event -> {
            contentController.setContent("settings");
        });
        helpPane.setOnMouseClicked(event -> {
            contentController.setContent("help");
        });
        logoutPane.setOnMouseClicked(event -> {
            UserAccount.logout();
            ScreenController.activate("Signin");
            ScreenController.getStage().setTitle("Sign in");
            ScreenController.centerStage();
        });

    }

    class ContentController {
        private HashMap<String, Pane> contentMap = new HashMap<>();
        
        public ContentController() {
            try{
            contentMap.put("dashboard", FXMLLoader.load(getClass().getResource("../views/DashboardContent.fxml")));
            contentMap.put("accounts", new Pane());
            contentMap.put("settings", FXMLLoader.load(getClass().getResource("../views/SettingsContent.fxml")));
            contentMap.put("help", FXMLLoader.load(getClass().getResource("../views/HelpContent.fxml")));
            } catch (IOException e){
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred while loading the content");
                alert.showAndWait();
            }
        }

        public void setContent(String key) {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(contentMap.get(key));
        }
    }
    
}
