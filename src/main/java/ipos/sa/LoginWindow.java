package ipos.sa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * This is the LoginWindow class
 * This defines the UI and behaviour of the Login window.
 * Programmed by: Tom
 */

public class LoginWindow extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("IPOS-SA");
        stage.setScene(scene);
        stage.show();
    }
}