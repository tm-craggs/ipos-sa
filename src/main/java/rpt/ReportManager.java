package rpt;

import db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportManager {

    // generates report of all invoices within a given period
    public String generateAllInvoicesReport(String startDate, String endDate) {
        StringBuilder report = new StringBuilder();

        report.append("=== All Invoices Report ===\n");
        report.append("From: ").append(startDate)
              .append(" To: ").append(endDate).append("\n\n");

        String sql = """
            SELECT invoice_id, merchant_id, amount, invoice_date, payment_status
            FROM invoices
            WHERE invoice_date BETWEEN ? AND ?
            ORDER BY invoice_date
        """;

        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            ResultSet rs = pstmt.executeQuery();

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                report.append("Invoice ID: ").append(rs.getString("invoice_id"))
                      .append(" | Merchant: ").append(rs.getString("merchant_id"))
                      .append(" | Amount: ").append(rs.getDouble("amount"))
                      .append(" | Date: ").append(rs.getString("invoice_date"))
                      .append(" | Payment: ").append(rs.getString("payment_status"))
                      .append("\n");
            }

            if (!hasResults) {
                report.append("No invoices found for this period.\n");
            }

        } catch (SQLException e) {
            report.append("Error generating report: ").append(e.getMessage());
        }

        return report.toString();
    }

    // generates invoice report for a specific merchant
    public String generateMerchantInvoicesReport(String merchantId, String startDate, String endDate) {
        StringBuilder report = new StringBuilder();

        report.append("=== Merchant Invoice Report ===\n");
        report.append("Merchant: ").append(merchantId).append("\n");
        report.append("From: ").append(startDate)
              .append(" To: ").append(endDate).append("\n\n");

        String sql = """
            SELECT invoice_id, amount, invoice_date, payment_status
            FROM invoices
            WHERE merchant_id = ? AND invoice_date BETWEEN ? AND ?
            ORDER BY invoice_date
        """;

        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, merchantId);
            pstmt.setString(2, startDate);
            pstmt.setString(3, endDate);

            ResultSet rs = pstmt.executeQuery();

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                report.append("Invoice ID: ").append(rs.getString("invoice_id"))
                      .append(" | Amount: ").append(rs.getDouble("amount"))
                      .append(" | Date: ").append(rs.getString("invoice_date"))
                      .append(" | Payment: ").append(rs.getString("payment_status"))
                      .append("\n");
            }

            if (!hasResults) {
                report.append("No invoices found for this merchant.\n");
            }

        } catch (SQLException e) {
            report.append("Error generating report: ").append(e.getMessage());
        }

        return report.toString();
    }

    // generates turnover report (goods sold + revenue)
    public String generateTurnoverReport(String startDate, String endDate) {
        StringBuilder report = new StringBuilder();

        report.append("=== Turnover Report ===\n");
        report.append("From: ").append(startDate)
              .append(" To: ").append(endDate).append("\n\n");

        // placeholder data
        report.append("Total Goods Sold: 0\n");
        report.append("Total Revenue: 0\n");

        return report.toString();
    }

    // generates list of orders for a merchant
    public String generateMerchantOrdersReport(String merchantId, String startDate, String endDate) {
        StringBuilder report = new StringBuilder();

        report.append("=== Merchant Orders Report ===\n");
        report.append("Merchant: ").append(merchantId).append("\n");
        report.append("From: ").append(startDate)
              .append(" To: ").append(endDate).append("\n\n");

        // placeholder data
        report.append("Order ID: ORD001 | Date: 2026-01-01 | Value: 100 | Dispatch: 2026-01-02 | Paid\n");
        report.append("Order ID: ORD002 | Date: 2026-01-05 | Value: 200 | Dispatch: 2026-01-06 | Pending\n");
        report.append("\nTotal Orders Value: 300\n");

        return report.toString();
    }

    // generates detailed activity report for a merchant
    public String generateMerchantActivityReport(String merchantId, String startDate, String endDate) {
        StringBuilder report = new StringBuilder();

        report.append("=== Merchant Activity Report ===\n");
        report.append("Merchant: ").append(merchantId).append("\n");
        report.append("From: ").append(startDate)
              .append(" To: ").append(endDate).append("\n\n");

        // placeholder data
        report.append("Order: ORD001 | Item: DrugA | Qty: 2 | Cost: 50 | Total: 100 | Paid\n");

        return report.toString();
    }

    // generates stock turnover report (sold vs received)
    public String generateStockTurnoverReport(String startDate, String endDate) {
        StringBuilder report = new StringBuilder();

        report.append("=== Stock Turnover Report ===\n");
        report.append("From: ").append(startDate)
              .append(" To: ").append(endDate).append("\n\n");

        // placeholder data
        report.append("Stock Sold: 0\n");
        report.append("Stock Received: 0\n");

        return report.toString();
    }
}