package ipos.sa;

import db.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.animation.TranslateTransition;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;


public class LoginController {

    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;

    @FXML
    protected void handleLogin() {

        // lock button during login
        loginButton.setDisable(true);

        String user = userField.getText();
        String pass = passwordField.getText();
        String accountType = DatabaseManager.verifyUser(user, pass);

        if (accountType != null) {
            UserSession.login(user, accountType);
            openMainMenu();
            closeWindow();
        } else {
            statusLabel.setText("Invalid credentials.");
            statusLabel.setTextFill(Color.RED);

            // shake status label on invalid login attempt to provide visual feedback
            shakeNode(statusLabel);
            loginButton.setDisable(false);
        }

    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }

    private void closeWindow() {

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();

    }

    private void openMainMenu(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("IPOS Main Menu");

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}