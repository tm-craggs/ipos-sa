package ipos.sa;

import db.DatabaseManager;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class LoginController {

    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;

    @FXML
    protected void handleLogin(ActionEvent event) {
        loginButton.setDisable(true);

        String user = userField.getText();
        String pass = passwordField.getText();
        String accountType = DatabaseManager.verifyUser(user, pass);

        if (accountType != null) {
            UserSession.login(user, accountType);

            SceneSwitcher.switchScene(event, "main-menu.fxml", "IPOS Main Menu");

        } else {
            statusLabel.setText("Invalid credentials.");
            statusLabel.setTextFill(Color.RED);
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

}