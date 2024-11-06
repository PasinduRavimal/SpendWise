package com.spendWise.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;

public class HomeController implements Initializable {

    @FXML
    private TitledPane dashboardPane;
    @FXML
    private TitledPane accountsPane;
    @FXML private TitledPane settingsPane;
    @FXML private TitledPane helpPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dashboardPane.setOnMouseClicked(event -> {
            System.out.println("Dashboard clicked");
        });

    }
    
}
