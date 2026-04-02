package rpt;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;

public class ReportWindow {

@FXML private ChoiceBox<String> reportTypeChoice;
@FXML private TextField startDateField;
@FXML private TextField endDateField;
@FXML private TextField merchantIdField;
@FXML private TextField invoiceIdField;
@FXML private TextArea reportOutputArea;

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

private void showError(String message) {
Alert alert = new Alert(Alert.AlertType.ERROR);
alert.setContentText(message);
alert.show();
}


}