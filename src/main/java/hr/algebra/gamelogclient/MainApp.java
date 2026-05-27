package hr.algebra.gamelogclient;

import hr.algebra.gamelogclient.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setPrimaryStage(stage);

        stage.setTitle("GameLog Client");
        stage.setResizable(true);

        SceneManager.switchTo("login", 400, 380);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}