package com.spendWise;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.*;

import com.spendWise.util.*;

public class SpalshScreen extends Application {

    private Text loadingLabel = new Text("Loading...");
    private ImageView image = new ImageView(SpalshScreen.class.getResource("resources/splashscreen.png").toString());

    @Override
    public void start(Stage primaryStage) {

        Bounds imageBounds = image.getBoundsInParent();

        loadingLabel.setX(25);
        loadingLabel.setY(200);
        loadingLabel.setFont(Font.font("System", null, FontPosture.ITALIC, 12));

        Pane root = new Pane(image, loadingLabel);
        double imageWidth = imageBounds.getWidth();
        double imageHeight = imageBounds.getHeight(); 
        root.setPrefSize(imageWidth, imageHeight);

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
        if (loadApplication()){

        } else {
            Alert alert = new Alert(AlertType.ERROR, "Couldn't load the application");
            alert.showAndWait();

            Platform.exit();
        };
    }

    private boolean isDatabaseConnected(){
        try {
            if (DatabaseConnection.getConnection() != null){
                return true;
            };
            return false;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean loadApplication() {

        loadingLabel.setText("Connecting to the database...");
        if (isDatabaseConnected()){
            loadingLabel.setText("Database connected...");
            try {
                if (!DatabaseConnection.isTablesExist()){
                    loadingLabel.setText("Setting up for the first use...");
                    DatabaseConnection.setupDatabase();
                    loadingLabel.setText("Loading GUI...");
                    return true;
                } else {
                    loadingLabel.setText("Loading GUI...");
                    return true;
                }
            } catch (Exception e){
                return false;
            }
        } else {
            return false;
        }
    }

    public static void main(String[] args) {

        launch(args);
    }

}
