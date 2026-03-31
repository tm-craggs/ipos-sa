package ipos.sa;

import db.DatabaseManager;
import javafx.application.Application;
import api.service.Server;

public class Launcher {
    public static void main(String[] args) {

        // connect to SQLite database
        DatabaseManager.connect();

        // start the REST API server
        Server.start();

        // launch GUI
        Application.launch(LoginWindow.class, args);
    }
}