package hr.algebra.gamelogclient.controller;

import hr.algebra.gamelogclient.api.ApiClient;
import hr.algebra.gamelogclient.api.ApiException;
import hr.algebra.gamelogclient.model.Game;
import hr.algebra.gamelogclient.util.SceneManager;
import hr.algebra.gamelogclient.util.Session;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class GamesController {

    @FXML private Label userLabel;
    @FXML private Button refreshButton;
    @FXML private Button newButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button backupButton;
    @FXML private Button restoreButton;
    @FXML private Button logoutButton;

    @FXML private TableView<Game> gamesTable;
    @FXML private TableColumn<Game, Number> idCol;
    @FXML private TableColumn<Game, String> titleCol;
    @FXML private TableColumn<Game, String> developerCol;
    @FXML private TableColumn<Game, String> platformCol;
    @FXML private TableColumn<Game, String> statusCol;
    @FXML private TableColumn<Game, Number> ratingCol;
    @FXML private TableColumn<Game, Number> hoursCol;

    @FXML private Label statusLabel;

    private final ObservableList<Game> games = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        userLabel.setText("Logged in as: " + Session.getUsername());

        configureColumns();
        gamesTable.setItems(games);

        gamesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
        });

        loadGames();
    }

    private void configureColumns() {
        idCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getId() == null ? 0 : c.getValue().getId().intValue())
        );
        titleCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitle())
        );
        developerCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDeveloper())
        );
        platformCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPlatform())
        );
        statusCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus())
        );
        ratingCol.setCellValueFactory(c -> {
            Integer r = c.getValue().getRating();
            return new SimpleObjectProperty<>(r == null ? 0 : r);
        });
        hoursCol.setCellValueFactory(c -> {
            Integer h = c.getValue().getHoursPlayed();
            return new SimpleObjectProperty<>(h == null ? 0 : h);
        });
    }

    @FXML
    private void handleRefresh() {
        loadGames();
    }

    @FXML
    private void handleLogout() {
        ApiClient.logout();
        try {
            SceneManager.switchTo("login", 400, 380);
        } catch (Exception e) {
            statusLabel.setText("Failed to return to login: " + e.getMessage());
        }
    }

    @FXML private void handleNew() {}
    @FXML private void handleEdit() {}
    @FXML private void handleDelete() {}
    @FXML private void handleBackup() {}
    @FXML private void handleRestore() {}

    private void loadGames() {
        setBusy(true, "Loading games...");

        new Thread(() -> {
            try {
                List<Game> result = ApiClient.getAllGames();
                Platform.runLater(() -> {
                    games.setAll(result);
                    setBusy(false, "Loaded " + result.size() + " games.");
                });
            } catch (ApiException e) {
                Platform.runLater(() -> setBusy(false, "Failed to load: " + e.getMessage()));
            }
        }).start();
    }

    private void setBusy(boolean busy, String msg) {
        refreshButton.setDisable(busy);
        statusLabel.setText(msg);
        statusLabel.setStyle(busy ? "-fx-text-fill: gray;" : "-fx-text-fill: green;");
        if (msg.toLowerCase().contains("fail")) {
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}