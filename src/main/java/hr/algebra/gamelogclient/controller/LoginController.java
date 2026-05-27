package hr.algebra.gamelogclient.controller;

import hr.algebra.gamelogclient.api.ApiClient;
import hr.algebra.gamelogclient.api.ApiException;
import hr.algebra.gamelogclient.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password are required.");
            return;
        }

        loginButton.setDisable(true);
        errorLabel.setText("Signing in...");
        errorLabel.setStyle("-fx-text-fill: gray;");

        new Thread(() -> {
            try {
                ApiClient.login(username, password);
                Platform.runLater(() -> {
                    try {
                        SceneManager.switchTo("games", 900, 600);
                    } catch (Exception ex) {
                        showError("Failed to open games screen: " + ex.getMessage());
                    }
                });
            } catch (ApiException e) {
                String msg = e.getStatusCode() == 401
                        ? "Invalid username or password."
                        : e.getMessage();
                Platform.runLater(() -> showError(msg));
            }
        }).start();
    }

    private void showError(String msg) {
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setText(msg);
        loginButton.setDisable(false);
    }
}