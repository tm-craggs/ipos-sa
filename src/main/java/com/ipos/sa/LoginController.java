package com.ipos.sa;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

public class LoginController {

    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    protected void handleLogin() {
        String user = userField.getText();
        String pass = passwordField.getText();

        if (user.equals("admin") && pass.equals("1234")) {
            statusLabel.setText("Login Successful!");
            statusLabel.setTextFill(Color.GREEN);
            // Here is where you'd trigger the "Report Generation" window
        } else {
            statusLabel.setText("Invalid credentials.");
            statusLabel.setTextFill(Color.RED);
        }
    }
}