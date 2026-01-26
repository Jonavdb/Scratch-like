package scratch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Hello ANC4 2526");
        label.setFont(new Font(25));
        Pane pane = new StackPane();
        pane.getChildren().add(label);
        Scene scene = new Scene(pane, 640, 480);
        primaryStage.setTitle("Scratch");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}