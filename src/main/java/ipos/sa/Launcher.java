package ipos.sa;

import db.DatabaseManager;
import javafx.application.Application;
import rpt.ReportManager; // ADD THIS

public class Launcher {
    public static void main(String[] args) {
        DatabaseManager.connect();

        //  TEMP TEST FOR RPT
        ReportManager rm = new ReportManager();
        String report = rm.generateAllInvoicesReport("2026-01-01", "2026-01-31");
        System.out.println(report);

        Application.launch(LoginWindow.class, args);
    }
}