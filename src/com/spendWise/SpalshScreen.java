package com.spendWise;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import com.spendWise.util.*;

public class SpalshScreen extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ImageView image = new ImageView(SpalshScreen.class.getResource("resources/splashscreen.png").toString());
        Bounds imageBounds = image.getBoundsInParent();

        Pane root = new Pane(image);
        root.setPrefSize(imageBounds.getWidth(), imageBounds.getHeight());

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        // Turn off control box
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

        // Center on screen
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);


        // Loading components
        if (isDatabaseConnected()){
            this.stop();
            Dashboard.main(null);
        } else {
            Alert alert = new Alert(AlertType.ERROR, "Couldn't establish the database connection.");
            alert.showAndWait();

            Platform.exit();
        }
    }

    private boolean isDatabaseConnected(){
        try {
            Dashboard.connection = DatabaseConnection.getConnection();
            return true;

        } catch (Exception e){
            return false;
        }
    }

    public static void main(String[] args) {

        launch(args);
    }

}
