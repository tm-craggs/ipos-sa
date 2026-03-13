package ipos.sa;

import db.DatabaseManager;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        DatabaseManager.connect();
        Application.launch(LoginWindow.class, args);
    }
}
