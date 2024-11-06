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

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.*;

import com.spendWise.controllers.ScreenController;
import com.spendWise.models.UserAccount;
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

    private boolean isDatabaseConnected() throws IOException, SQLException {
        try {
            if (DatabaseConnection.getConnection() != null){
                return true;
            };
            return false;

        } catch (IOException e) {
            throw new IOException("Need permission to access file system.");
        }
    }

    private void loadApplication () {
        if (task != null)
            return;

        task = new Task<>() {
            public Boolean call() {
                try {
                    updateMessage("Connecting to the database...");

                    if (isDatabaseConnected()){
                        updateMessage("Database connected...");

                        if (!DatabaseConnection.doTablesExist()){
                            updateMessage("Setting up for the first use...");

                            DatabaseConnection.setupDatabase();
                            updateMessage("Loading GUI...");

                            loadSignup();

                            return true;
                        } else if (UserAccount.doUsersExist()){
                            updateMessage("Loading GUI...");
                            loadSignin();

                            return true;
                        } else {
                            updateMessage("Loading GUI...");

                            loadSignup();

                            return true;
                        }
                    } else {
                        return false;
                    }
                } catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
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
            onError(task.getException().getMessage());
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
                ;
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

    private void onError(String message) {
        Platform.runLater(() -> {
            if (status == 1){
                ;
            } else {
                Alert alert = new Alert(AlertType.ERROR, message);
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
                ScreenController.getInstance();
                ScreenController.activate("Signin");
                Stage stage = ScreenController.stage;
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
                
            } catch (IOException e){
                Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        });
    }

    private void loadSignup() {
        Platform.runLater(() -> {
            try{
                ScreenController.getInstance();
                ScreenController.activate("Signup");
                Stage stage = ScreenController.stage;
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
                
            } catch (IOException e){
                Alert alert = new Alert(AlertType.ERROR, e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {

        launch(args);
    }

}
