package ipos.sa;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

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

        if (user.equals("admin") && pass.equals("1234")) {
            statusLabel.setText("Login Successful!");
            statusLabel.setTextFill(Color.GREEN);
            // Here is where you'd trigger the "Report Generation" window
        } else {
            statusLabel.setText("Invalid credentials.");
            statusLabel.setTextFill(Color.RED);

            // shake status label on invalid login attempt to provide visual feedback
            shakeNode(statusLabel);
        }

        // unlock button
        loginButton.setDisable(false);
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