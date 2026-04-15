package rpt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.print.PrinterJob;
import ipos.sa.SceneSwitcher;
import ipos.sa.UserSession;

public class ReportWindow {

    @FXML
    private ChoiceBox<String> reportTypeChoice;
    @FXML
    private TextField startDateField;
    @FXML
    private TextField endDateField;
    @FXML
    private TextField merchantIdField;
    @FXML
    private TextField invoiceIdField;
    @FXML
    private TextArea reportOutputArea;

    private final ReportManager reportManager = new ReportManager();

    @FXML
    public void initialize() {
        reportTypeChoice.getItems().addAll(
                "All Invoices",
                "Merchant Invoices",
                "Turnover",
                "Merchant Orders",
                "Merchant Activity",
                "Stock Turnover"
        );
        reportTypeChoice.setValue("All Invoices");
    }

    @FXML
    private void handleGenerateReport() {
        String reportType = reportTypeChoice.getValue();
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();
        String merchantId = merchantIdField.getText();

        String result = "";

        switch (reportType) {
            case "All Invoices":
                result = reportManager.generateAllInvoicesReport(startDate, endDate);
                break;

            case "Merchant Invoices":
                result = reportManager.generateMerchantInvoicesReport(merchantId, startDate, endDate);
                break;

            case "Turnover":
                result = reportManager.generateTurnoverReport(startDate, endDate);
                break;

            case "Merchant Orders":
                result = reportManager.generateMerchantOrdersReport(merchantId, startDate, endDate);
                break;

            case "Merchant Activity":
                result = reportManager.generateMerchantActivityReport(merchantId, startDate, endDate);
                break;

            case "Stock Turnover":
                result = reportManager.generateStockTurnoverReport(startDate, endDate);
                break;
        }

        reportOutputArea.setText(result);
    }

    @FXML
    private void handleInvoiceDetails() {
        String invoiceId = invoiceIdField.getText();

        if (invoiceId == null || invoiceId.isBlank()) {
            reportOutputArea.setText("Please enter an invoice ID.");
            return;
        }

        String result = reportManager.generateInvoiceDetailsReport(invoiceId);
        reportOutputArea.setText(result);
    }

    @FXML
    private void handlePrint() {
        String content = reportOutputArea.getText();

        if (content == null || content.isBlank()) {
            reportOutputArea.setText("No report to print.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null && job.showPrintDialog(reportOutputArea.getScene().getWindow())) {
            boolean success = job.printPage(reportOutputArea);

            if (success) {
                job.endJob();
            } else {
                showError("Printing failed.");
            }
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/ipos/sa/main-menu.fxml", "IPOS-SA - Main Menu");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // confirm logout
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        // if user confirms, logout UserSession and return to login screen
        if (confirm.getResult() == ButtonType.YES) {
            UserSession.logout();
            SceneSwitcher.switchScene(event, "/ipos/sa/login-window.fxml", "Login");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }


}