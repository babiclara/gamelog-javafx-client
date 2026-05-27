package hr.algebra.gamelogclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Label hello = new Label("Hello GameLog Client — JavaFX is working!");
        StackPane root = new StackPane(hello);
        Scene scene = new Scene(root, 500, 200);

        stage.setTitle("GameLog Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}