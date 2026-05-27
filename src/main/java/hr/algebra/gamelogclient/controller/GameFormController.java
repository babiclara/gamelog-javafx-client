package hr.algebra.gamelogclient.controller;

import hr.algebra.gamelogclient.api.ApiClient;
import hr.algebra.gamelogclient.api.ApiException;
import hr.algebra.gamelogclient.model.Game;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class GameFormController {

    @FXML private Label titleLabel;
    @FXML private Label errorLabel;

    @FXML private TextField titleField;
    @FXML private TextField developerField;
    @FXML private TextField publisherField;
    @FXML private Spinner<Integer> releaseYearSpinner;
    @FXML private ComboBox<String> genreCombo;
    @FXML private ComboBox<String> platformCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> difficultyCombo;
    @FXML private Spinner<Integer> hoursSpinner;
    @FXML private Spinner<Integer> completionSpinner;
    @FXML private Spinner<Integer> ratingSpinner;
    @FXML private DatePicker startedDatePicker;
    @FXML private DatePicker finishedDatePicker;
    @FXML private TextArea reviewArea;
    @FXML private TextArea notesArea;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Game existingGame;
    private Runnable onSavedCallback;

    @FXML
    public void initialize() {
        releaseYearSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1980, 2030, 2024));
        hoursSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0));
        completionSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        ratingSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3));

        genreCombo.setItems(FXCollections.observableArrayList(
                "RPG", "ACTION_RPG", "SOULSLIKE", "FPS", "SHOOTER", "PLATFORMER",
                "METROIDVANIA", "SURVIVAL", "SURVIVAL_HORROR", "SIMULATION",
                "STRATEGY", "GRAND_STRATEGY", "ROGUELIKE", "FIGHTING", "SPORTS",
                "RACING", "PUZZLE", "VISUAL_NOVEL", "OPEN_WORLD", "MMORPG",
                "BATTLE_ROYALE", "MOBA", "INDIE"
        ));
        platformCombo.setItems(FXCollections.observableArrayList(
                "PC_STEAM", "PC_EPIC", "PC_GOG", "PC_GAMEPASS",
                "PS5", "PS4", "XBOX_SERIES", "XBOX_ONE", "SWITCH",
                "STEAM_DECK", "MOBILE_IOS", "MOBILE_ANDROID",
                "VR_QUEST", "VR_PSVR", "RETRO_HANDHELD", "BROWSER"
        ));
        statusCombo.setItems(FXCollections.observableArrayList(
                "PLAYING", "BEATEN", "COMPLETED_100", "ABANDONED", "ON_HOLD",
                "BACKLOG", "WISHLIST", "REPLAYING"
        ));
        difficultyCombo.setItems(FXCollections.observableArrayList(
                "STORY", "EASY", "NORMAL", "HARD", "NIGHTMARE", "SOULSBORNE"
        ));
    }

    public void setGame(Game g) {
        this.existingGame = g;
        if (g == null) {
            titleLabel.setText("New Game");
            return;
        }
        titleLabel.setText("Edit Game: " + g.getTitle());
        titleField.setText(g.getTitle());
        developerField.setText(g.getDeveloper());
        publisherField.setText(g.getPublisher());
        if (g.getReleaseYear() != null) releaseYearSpinner.getValueFactory().setValue(g.getReleaseYear());
        genreCombo.setValue(g.getGenre());
        platformCombo.setValue(g.getPlatform());
        statusCombo.setValue(g.getStatus());
        difficultyCombo.setValue(g.getDifficulty());
        if (g.getHoursPlayed() != null) hoursSpinner.getValueFactory().setValue(g.getHoursPlayed());
        if (g.getCompletionPercent() != null) completionSpinner.getValueFactory().setValue(g.getCompletionPercent());
        if (g.getRating() != null) ratingSpinner.getValueFactory().setValue(g.getRating());
        startedDatePicker.setValue(g.getStartedDate());
        finishedDatePicker.setValue(g.getFinishedDate());
        reviewArea.setText(g.getReview());
        notesArea.setText(g.getPersonalNotes());
    }

    public void setOnSaved(Runnable callback) {
        this.onSavedCallback = callback;
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleSave() {
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errorLabel.setText("Title is required.");
            return;
        }
        if (genreCombo.getValue() == null) { errorLabel.setText("Genre is required."); return; }
        if (platformCombo.getValue() == null) { errorLabel.setText("Platform is required."); return; }
        if (statusCombo.getValue() == null) { errorLabel.setText("Status is required."); return; }

        Game payload = (existingGame != null) ? existingGame : new Game();
        payload.setTitle(titleField.getText().trim());
        payload.setDeveloper(blankToNull(developerField.getText()));
        payload.setPublisher(blankToNull(publisherField.getText()));
        payload.setReleaseYear(releaseYearSpinner.getValue());
        payload.setGenre(genreCombo.getValue());
        payload.setPlatform(platformCombo.getValue());
        payload.setStatus(statusCombo.getValue());
        payload.setDifficulty(difficultyCombo.getValue());
        payload.setHoursPlayed(hoursSpinner.getValue());
        payload.setCompletionPercent(completionSpinner.getValue());
        payload.setRating(ratingSpinner.getValue());
        payload.setStartedDate(startedDatePicker.getValue());
        payload.setFinishedDate(finishedDatePicker.getValue());
        payload.setReview(blankToNull(reviewArea.getText()));
        payload.setPersonalNotes(blankToNull(notesArea.getText()));

        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        errorLabel.setStyle("-fx-text-fill: gray;");
        errorLabel.setText("Saving...");

        new Thread(() -> {
            try {
                if (existingGame == null) {
                    ApiClient.createGame(payload);
                } else {
                    ApiClient.updateGame(existingGame.getId(), payload);
                }
                Platform.runLater(() -> {
                    if (onSavedCallback != null) onSavedCallback.run();
                    closeWindow();
                });
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    errorLabel.setStyle("-fx-text-fill: red;");
                    errorLabel.setText("Save failed: " + e.getMessage());
                    saveButton.setDisable(false);
                    cancelButton.setDisable(false);
                });
            }
        }).start();
    }

    private void closeWindow() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }

    private static String blankToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}