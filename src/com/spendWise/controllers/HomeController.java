package com.spendWise.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.spendWise.models.Account;
import com.spendWise.models.UserAccount;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;

public class HomeController implements Initializable {

    private static TitledPane accountsStaticPane;
    private static HomeController instance;

    @FXML
    private TitledPane dashboardPane;
    @FXML
    private TitledPane accountsPane;
    @FXML
    private TitledPane settingsPane;
    @FXML
    private TitledPane helpPane;
    @FXML
    private Pane contentPane;
    @FXML
    private TitledPane logoutPane;

    private ContentController contentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contentController = new ContentController();
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

        accountsStaticPane = accountsPane;
        instance = this;

    }

    public static HomeController getInstance(){
        return instance;
    }

    public synchronized void addAccounts(){
        Platform.runLater(() -> {
            try {
                AnchorPane anchorPane = (AnchorPane) accountsStaticPane.getContent();
                VBox vbox = new VBox();
                vbox.setPrefHeight(180);
                vbox.setPrefWidth(200);
                for (Account account : Account.getAccountsList()){
                    TitledPane titledPane = new TitledPane(account.getAccountName(), null);
                    titledPane.setCollapsible(false);
                    titledPane.setExpanded(false);
                    titledPane.setMaxWidth(190);

                    titledPane.setOnMouseClicked(event -> {
                        FXMLLoader loader = new FXMLLoader(HomeController.class.getResource("../views/AccountContent.fxml"));
                        AccountController controller = new AccountController();
                        controller.setAccount(account);
                        loader.setController(controller);
                        try{
                            Pane root = loader.load();
                            contentController.setContentByFXML(root);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load account content");
                            alert.showAndWait();
                        }
                    });

                    vbox.getChildren().add(titledPane);
                }
                ScrollPane scrollPane = new ScrollPane(vbox);
                scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
                anchorPane.getChildren().add(scrollPane);
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.showAndWait();
            }
        });
    }

    class ContentController {
        private HashMap<String, Pane> contentMap = new HashMap<>();

        public ContentController() {
            try {
                contentMap.put("dashboard", FXMLLoader.load(getClass().getResource("../views/DashboardContent.fxml")));
                contentMap.put("accounts", new Pane());
                contentMap.put("settings", FXMLLoader.load(getClass().getResource("../views/SettingsContent.fxml")));
                contentMap.put("help", FXMLLoader.load(getClass().getResource("../views/HelpContent.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred while loading the content");
                alert.showAndWait();
            }
        }

        public void setContent(String key) {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(contentMap.get(key));
        }

        public void setContentByFXML(Pane root){
            contentPane.getChildren().clear();
            contentPane.getChildren().add(root);
        }
    }

}
