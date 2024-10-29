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
import javafx.concurrent.*;
import javafx.fxml.FXMLLoader;

import java.util.concurrent.*;

import com.spendWise.util.*;

public class SpalshScreen extends Application {

    private Text loadingLabel = new Text("Loading...");
    private ImageView image = new ImageView(SpalshScreen.class.getResource("resources/splashscreen.png").toString());
    private Task<Boolean> task;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private short status = 0;
    private Stage primaryStage;
    private Rectangle2D primScreenBounds;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        primaryStage.setOnCloseRequest(event -> {
            if (task != null)
                task.cancel();
            
            executor.shutdown();
        });

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
        primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

        loadApplication();
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

    private void loadApplication () {
        if (task != null)
            return;

        task = new Task<>() {
            public Boolean call() {
                try {
                    updateMessage("Connecting to the database...");
                    Thread.sleep(1000);

                    if (isDatabaseConnected()){
                        updateMessage("Database connected...");
                        Thread.sleep(1000);

                        if (!DatabaseConnection.isTablesExist()){
                            updateMessage("Setting up for the first use...");
                            Thread.sleep(1000);

                            DatabaseConnection.setupDatabase();
                            updateMessage("Loading GUI...");
                            Thread.sleep(1000);

                            loadSignup();

                            return true;
                        } else if (DatabaseConnection.isUsersExist()){
                            // TODO: Remove this line
                            DatabaseConnection.addDescriptionColumn();
                            updateMessage("Loading GUI...");
                            Thread.sleep(1000);

                            loadSignin();

                            return true;
                        } else {
                            // TODO: Remove this line
                            DatabaseConnection.addDescriptionColumn();
                            updateMessage("Loading GUI...");
                            Thread.sleep(1000);

                            loadSignup();

                            return true;
                        }
                    } else {
                        return false;
                    }
                } catch (Exception e){
                    return false;
                }
            }
        };

        executor.execute(task);

        task.setOnScheduled(event -> {
            loadingLabel.setText("Loading Application...");
        });

        task.setOnRunning(event -> {
            loadingLabel.textProperty().bind(task.messageProperty());
        });

        task.setOnFailed(event -> {
            loadingLabel.textProperty().unbind();
            onError();
            task = null;
        });

        task.setOnCancelled(event -> {
            loadingLabel.textProperty().unbind();
            onError();
            task = null;
        });

        task.setOnSucceeded(event -> {
            loadingLabel.textProperty().unbind();
            if (task.getValue() == true)
            ;
            else {
                onError();
            }
            task = null;
        });

    }

    private void onError() {
        Platform.runLater(() -> {
            if (status == 1){

            } else {
                Alert alert = new Alert(AlertType.ERROR, "Couldn't load the application");
                alert.showAndWait();

                if (task != null)
                    task.cancel();
            
                executor.shutdown();
                Platform.exit();
            };
        });
    }

    private void loadSignin() {
        Platform.runLater(() -> {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(this.getClass().getResource("views/signin.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setOnCloseRequest(event -> {
                    if (task != null)
                        task.cancel();
            
                    executor.shutdown();
                    Platform.exit();
                });
                stage.setTitle("Sign in");
                primaryStage.close();
                stage.show();

                stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
                
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void loadSignup() {
        Platform.runLater(() -> {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(this.getClass().getResource("views/signup.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setOnCloseRequest(event -> {
                    if (task != null)
                        task.cancel();
            
                    executor.shutdown();
                    Platform.exit();
                });
                stage.setTitle("Sign up");
                primaryStage.close();
                stage.show();

                stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
                
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {

        launch(args);
    }

}
