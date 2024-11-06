package com.spendWise.controllers;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ScreenController {
    private static ConcurrentHashMap<String, Scene> screenMap;
    public static Stage stage = new Stage();
    private static Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();

    public static synchronized void getInstance() throws IOException {
        if (screenMap != null) {
            return;
        }

        screenMap = new ConcurrentHashMap<>();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
        });

        try {
            Parent root = FXMLLoader.load(ScreenController.class.getResource("../views/signin.fxml"));
            screenMap.put("Signin", new Scene(root));
            root = FXMLLoader.load(ScreenController.class.getResource("../views/signup.fxml"));
            screenMap.put("Signup", new Scene(root));
            root = FXMLLoader.load(ScreenController.class.getResource("../views/home.fxml"));
            screenMap.put("Home", new Scene(root));

        } catch (IOException e) {
            throw new IOException("Cannot access critical files.");
        }
    }

    private ScreenController(Scene main) {
    }

    public static void centerStage() {
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public static void activate(String name) {
        stage.hide();
        stage.setScene(screenMap.get(name));
        stage.show();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

}
