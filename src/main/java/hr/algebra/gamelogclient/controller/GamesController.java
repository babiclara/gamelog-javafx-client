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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

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
            boolean hasSelection = newSel != null;
            boolean isAdmin = isCurrentUserAdmin();
            editButton.setDisable(!hasSelection || !isAdmin);
            deleteButton.setDisable(!hasSelection || !isAdmin);
        });

        boolean admin = isCurrentUserAdmin();
        newButton.setDisable(!admin);
        backupButton.setDisable(!admin);
        restoreButton.setDisable(!admin);

        loadGames();
    }

    private boolean isCurrentUserAdmin() {
        return "admin".equals(Session.getUsername());
    }

    private void configureColumns() {
        idCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getId() == null ? 0 : c.getValue().getId().intValue())
        );
        titleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitle()));
        developerCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeveloper()));
        platformCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPlatform()));
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
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

    @FXML
    private void handleNew() {
        openGameForm(null);
    }

    @FXML
    private void handleEdit() {
        Game selected = gamesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        openGameForm(selected);
    }

    @FXML
    private void handleDelete() {
        Game selected = gamesTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete game");
        confirm.setHeaderText("Delete \"" + selected.getTitle() + "\"?");
        confirm.setContentText("This cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        setStatus("Deleting...", "gray");
        new Thread(() -> {
            try {
                ApiClient.deleteGame(selected.getId());
                Platform.runLater(() -> {
                    setStatus("Deleted \"" + selected.getTitle() + "\".", "green");
                    loadGames();
                });
            } catch (ApiException e) {
                Platform.runLater(() -> setStatus("Delete failed: " + e.getMessage(), "red"));
            }
        }).start();
    }

    @FXML
    private void handleBackup() {
        setStatus("Creating backup...", "gray");
        new Thread(() -> {
            try {
                String filename = ApiClient.createBackup();
                Platform.runLater(() -> {
                    setStatus("Backup created: " + filename, "green");
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Backup created");
                    info.setHeaderText("Database backup saved");
                    info.setContentText("Filename: " + filename
                            + "\n\nStored in the backups/ folder on the server.");
                    info.showAndWait();
                });
            } catch (ApiException e) {
                Platform.runLater(() -> setStatus("Backup failed: " + e.getMessage(), "red"));
            }
        }).start();
    }

    @FXML
    private void handleRestore() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Restore database");
        dialog.setHeaderText("Restore from backup file");
        dialog.setContentText("Enter the backup filename (e.g. backup-2026-05-27_12-54-01.sql):");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) return;

        String filename = result.get().trim();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Restore database");
        confirm.setHeaderText("Restore from " + filename + "?");
        confirm.setContentText("This will REPLACE all current data. Continue?");
        Optional<ButtonType> ok = confirm.showAndWait();
        if (ok.isEmpty() || ok.get() != ButtonType.OK) return;

        setStatus("Restoring database...", "gray");
        new Thread(() -> {
            try {
                String msg = ApiClient.restoreBackup(filename);
                Platform.runLater(() -> {
                    setStatus(msg, "green");
                    loadGames();
                });
            } catch (ApiException e) {
                Platform.runLater(() -> setStatus("Restore failed: " + e.getMessage(), "red"));
            }
        }).start();
    }

    private void openGameForm(Game existing) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/gameForm.fxml")
            );
            Parent root = loader.load();

            GameFormController controller = loader.getController();
            controller.setGame(existing);
            controller.setOnSaved(this::loadGames);

            Stage stage = new Stage();
            stage.setTitle(existing == null ? "New Game" : "Edit Game");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(SceneManager.getPrimaryStage());
            stage.showAndWait();
        } catch (Exception e) {
            setStatus("Could not open form: " + e.getMessage(), "red");
        }
    }

    private void loadGames() {
        setStatus("Loading games...", "gray");
        refreshButton.setDisable(true);

        new Thread(() -> {
            try {
                List<Game> result = ApiClient.getAllGames();
                Platform.runLater(() -> {
                    games.setAll(result);
                    setStatus("Loaded " + result.size() + " games.", "green");
                    refreshButton.setDisable(false);
                });
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    setStatus("Failed to load: " + e.getMessage(), "red");
                    refreshButton.setDisable(false);
                });
            }
        }).start();
    }

    private void setStatus(String msg, String color) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}