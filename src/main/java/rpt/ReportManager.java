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
    
        String sql = """
            SELECT COUNT(*) AS total_orders, SUM(order_value) AS total_revenue
            FROM orders
            WHERE order_date BETWEEN ? AND ?
        """;
    
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
    
            ResultSet rs = pstmt.executeQuery();
    
            if (rs.next()) {
                int totalOrders = rs.getInt("total_orders");
                double totalRevenue = rs.getDouble("total_revenue");
    
                report.append("Total Orders: ").append(totalOrders).append("\n");
                report.append("Total Revenue: ").append(totalRevenue).append("\n");
            } else {
                report.append("No data found.\n");
            }
    
        } catch (SQLException e) {
            report.append("Error generating report: ").append(e.getMessage());
        }
    
        return report.toString();
    }

   // generates list of orders for a merchant
public String generateMerchantOrdersReport(String merchantId, String startDate, String endDate) {
    StringBuilder report = new StringBuilder();

    report.append("=== Merchant Orders Report ===\n");
    report.append("Merchant: ").append(merchantId).append("\n");
    report.append("From: ").append(startDate)
          .append(" To: ").append(endDate).append("\n\n");

    String sql = """
        SELECT order_id, order_date, order_value, dispatch_date, payment_status
        FROM orders
        WHERE merchant_id = ? AND order_date BETWEEN ? AND ?
        ORDER BY order_date
    """;

    double totalValue = 0;

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

            String orderId = rs.getString("order_id");
            String orderDate = rs.getString("order_date");
            double orderValue = rs.getDouble("order_value");
            String dispatchDate = rs.getString("dispatch_date");
            String paymentStatus = rs.getString("payment_status");

            totalValue += orderValue;

            report.append("Order ID: ").append(orderId)
                  .append(" | Date: ").append(orderDate)
                  .append(" | Value: ").append(orderValue)
                  .append(" | Dispatch: ").append(dispatchDate)
                  .append(" | ").append(paymentStatus)
                  .append("\n");
        }

        if (!hasResults) {
            report.append("No orders found for this merchant.\n");
        } else {
            report.append("\nTotal Orders Value: ").append(totalValue).append("\n");
        }

    } catch (SQLException e) {
        report.append("Error generating report: ").append(e.getMessage());
    }

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


// generates stock turnover report (goods sold + revenue)
public String generateStockTurnoverReport(String startDate, String endDate) {
    StringBuilder report = new StringBuilder();

    report.append("=== Stock Turnover Report ===\n");
    report.append("From: ").append(startDate)
          .append(" To: ").append(endDate).append("\n\n");

    String sql = """
        SELECT COUNT(*) AS total_orders, SUM(order_value) AS total_revenue
        FROM orders
        WHERE order_date BETWEEN ? AND ?
    """;

    try {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, startDate);
        pstmt.setString(2, endDate);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int totalOrders = rs.getInt("total_orders");
            double totalRevenue = rs.getDouble("total_revenue");

            report.append("Total Orders (Goods Sold): ").append(totalOrders).append("\n");
            report.append("Total Revenue: ").append(totalRevenue).append("\n");
        } else {
            report.append("No data found for this period.\n");
        }

    } catch (SQLException e) {
        report.append("Error generating report: ").append(e.getMessage());
    }

    return report.toString();
}
}