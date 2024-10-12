import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;

public class App extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        Text text = new Text(69, 89, "Welcome to JavaFX UI Programming!");
        text.setFont(new Font(16));

        Pane pane = new Pane(text);
        pane.setPrefSize(400, 200);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }
    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
