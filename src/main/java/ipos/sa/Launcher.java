package ipos.sa;

import db.DatabaseManager;
import javafx.application.Application;
import rpt.ReportManager;

public class Launcher {
    public static void main(String[] args) {
        DatabaseManager.connect();
        Application.launch(LoginWindow.class, args);
    }
}