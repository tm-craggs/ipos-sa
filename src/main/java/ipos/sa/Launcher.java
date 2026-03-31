package ipos.sa;

import db.DatabaseManager;
import javafx.application.Application;
import rpt.ReportManager; // ADD THIS
import api.service.Server;

public class Launcher {
    public static void main(String[] args) {

        // connect to SQLite database
        DatabaseManager.connect();

        // start the REST API server
        Server.start();

        //  TEMP TEST FOR RPT
        ReportManager rm = new ReportManager();
        String report = rm.generateAllInvoicesReport("2026-01-01", "2026-01-31");
        System.out.println(report);

        Application.launch(LoginWindow.class, args);
    }
}