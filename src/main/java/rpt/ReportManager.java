package rpt;

public class ReportManager {

    public String generateAllInvoicesReport(String startDate, String endDate) {
        StringBuilder report = new StringBuilder();

        report.append("=== All Invoices Report ===\n");
        report.append("From: ").append(startDate)
              .append(" To: ").append(endDate).append("\n\n");

        // TODO: Replace with real database/system data
        report.append("Invoice ID: INV001 | Merchant: M001 | Amount: 100\n");
        report.append("Invoice ID: INV002 | Merchant: M002 | Amount: 200\n");

        return report.toString();
    }

    public String generateMerchantInvoicesReport(String merchantId, String startDate, String endDate) {
        StringBuilder report = new StringBuilder();

        report.append("=== Merchant Invoice Report ===\n");
        report.append("Merchant: ").append(merchantId).append("\n");
        report.append("From: ").append(startDate)
              .append(" To: ").append(endDate).append("\n\n");

        // TODO: Replace with real database/system data
        report.append("Invoice ID: INV010 | Amount: 150\n");
        report.append("Invoice ID: INV011 | Amount: 300\n");

        return report.toString();
    }
}