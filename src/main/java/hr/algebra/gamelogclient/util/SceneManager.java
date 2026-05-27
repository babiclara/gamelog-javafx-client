package hr.algebra.gamelogclient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public final class SceneManager {

    private static Stage primaryStage;

    private SceneManager() {}

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchTo(String fxmlName, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(
                        SceneManager.class.getResource("/fxml/" + fxmlName + ".fxml"),
                        "FXML not found: /fxml/" + fxmlName + ".fxml"
                )
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
    }
}