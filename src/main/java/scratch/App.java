package scratch;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scratch.model.ChosenActions;
import scratch.view.MainView;
import scratch.viewmodel.MainViewModel;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new MainView(new MainViewModel(new ChosenActions())));
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}