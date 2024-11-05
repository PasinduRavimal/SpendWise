package com.spendWise.controllers;

import java.util.concurrent.ConcurrentHashMap;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ScreenController {
    private static ConcurrentHashMap<String, Scene> screenMap = new ConcurrentHashMap<String, Scene>();
    public static Stage stage = new Stage();
    private static Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();

    public static synchronized void getInstance() {
        if (!screenMap.isEmpty()) {
            return;
        }

        try {
            Parent root = FXMLLoader.load(ScreenController.class.getResource("../views/signin.fxml"));
            screenMap.put("Signin", new Scene(root));
            root = FXMLLoader.load(ScreenController.class.getResource("../views/signup.fxml"));
            screenMap.put("Signup", new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
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
