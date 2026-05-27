package hr.algebra.gamelogclient;

import hr.algebra.gamelogclient.api.ApiClient;
import hr.algebra.gamelogclient.api.ApiException;
import hr.algebra.gamelogclient.model.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        Label status = new Label("Calling backend...");
        TextArea output = new TextArea();
        output.setEditable(false);
        output.setPrefRowCount(20);

        VBox root = new VBox(10, status, output);
        Scene scene = new Scene(root, 700, 500);

        stage.setTitle("GameLog Client — API Smoke Test");
        stage.setScene(scene);
        stage.show();

        new Thread(() -> {
            StringBuilder log = new StringBuilder();
            try {
                log.append("1. Logging in as admin...\n");
                ApiClient.login("admin", "admin123");
                log.append("   OK. Token starts with: ")
                        .append(hr.algebra.gamelogclient.util.Session.getAccessToken().substring(0, 30))
                        .append("...\n\n");

                log.append("2. GET /api/games\n");
                List<Game> games = ApiClient.getAllGames();
                log.append("   Got ").append(games.size()).append(" games:\n");
                for (Game g : games) {
                    log.append("   - [").append(g.getId()).append("] ")
                            .append(g.getTitle()).append(" (").append(g.getPlatform()).append(")\n");
                }
                log.append("\n");

                log.append("3. Triggering backup endpoint...\n");
                String filename = ApiClient.createBackup();
                log.append("   Backup created: ").append(filename).append("\n");

                javafx.application.Platform.runLater(() -> {
                    status.setText("All tests passed ✓");
                    output.setText(log.toString());
                });
            } catch (ApiException e) {
                log.append("\nERROR: ").append(e.getMessage()).append("\n");
                javafx.application.Platform.runLater(() -> {
                    status.setText("Test FAILED ✗");
                    output.setText(log.toString());
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}