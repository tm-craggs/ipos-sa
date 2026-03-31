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
    
                report.append("Total Orders Received: ").append(totalOrders).append("\n");
                report.append("Total Revenue: ").append(totalRevenue).append("\n");
            } else {
                report.append("No data found.\n");
            }
    
        } catch (SQLException e) {
            report.append("Error generating report: ").append(e.getMessage());
        }
    
        return report.toString();
    }

   // generates list of orders for a merchant (Appendix-style)
public String generateMerchantOrdersReport(String merchantId, String startDate, String endDate) {
    StringBuilder report = new StringBuilder();
    
    report.append("=== Merchant Orders Report ===\n");
    report.append("Client: ").append(merchantId).append("\n");
    report.append("Report Period: ").append(startDate)
    .append(" - ").append(endDate).append("\n\n");
    
    report.append("Order ID | Ordered | Amount | Dispatched | Delivered | Payment\n");
    report.append("-------------------------------------------------------------\n");
    
    String sql = """
    SELECT order_id, order_date, order_value, dispatch_date, delivered_date, payment_status
    FROM orders
    WHERE merchant_id = ? AND order_date BETWEEN ? AND ?
    ORDER BY order_date
    """;
    
    double totalValue = 0;
    int totalOrders = 0;
    int dispatchedCount = 0;
    int deliveredCount = 0;
    int paidCount = 0;
    
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
    String deliveredDate = rs.getString("delivered_date");
    String paymentStatus = rs.getString("payment_status");
    
    totalOrders++;
    totalValue += orderValue;
    
    if (dispatchDate != null && !dispatchDate.isBlank()) {
    dispatchedCount++;
    }
    
    if (deliveredDate != null && !deliveredDate.isBlank()) {
    deliveredCount++;
    }
    
    if (paymentStatus != null && paymentStatus.equalsIgnoreCase("PAID")) {
    paidCount++;
    }
    
    report.append(orderId).append(" | ")
    .append(orderDate).append(" | ")
    .append(orderValue).append(" | ")
    .append(dispatchDate == null ? "Pending" : dispatchDate).append(" | ")
    .append(deliveredDate == null ? "Pending" : deliveredDate).append(" | ")
    .append(paymentStatus)
    .append("\n");
    }
    
    if (!hasResults) {
    report.append("No orders found for this merchant.\n");
    } else {
    report.append("\n-------------------------------------------------------------\n");
    report.append("Total Orders: ").append(totalOrders)
    .append(" | Total Value: ").append(totalValue)
    .append(" | Dispatched: ").append(dispatchedCount)
    .append(" | Delivered: ").append(deliveredCount)
    .append(" | Paid: ").append(paidCount)
    .append("\n");
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
    
    String orderSql = """
    SELECT order_id, order_value, order_date, payment_status
    FROM orders
    WHERE merchant_id = ? AND order_date BETWEEN ? AND ?
    ORDER BY order_date
    """;
    
    String itemSql = """
    SELECT item_id, quantity, unit_cost, amount
    FROM order_items
    WHERE order_id = ?
    """;
    
    try {
    Connection conn = DatabaseManager.getConnection();
    PreparedStatement orderStmt = conn.prepareStatement(orderSql);
    orderStmt.setString(1, merchantId);
    orderStmt.setString(2, startDate);
    orderStmt.setString(3, endDate);
    
    ResultSet orderRs = orderStmt.executeQuery();
    
    boolean hasOrders = false;
    
    while (orderRs.next()) {
    hasOrders = true;
    
    String orderId = orderRs.getString("order_id");
    double orderValue = orderRs.getDouble("order_value");
    String orderDate = orderRs.getString("order_date");
    String paymentStatus = orderRs.getString("payment_status");
    
    report.append("Order ID: ").append(orderId)
    .append(" | Cost: ").append(orderValue)
    .append(" | Ordered: ").append(orderDate)
    .append(" | Payment: ").append(paymentStatus)
    .append("\n");
    
    PreparedStatement itemStmt = conn.prepareStatement(itemSql);
    itemStmt.setString(1, orderId);
    
    ResultSet itemRs = itemStmt.executeQuery();
    
    boolean hasItems = false;
    
    while (itemRs.next()) {
    hasItems = true;
    
    int itemId = itemRs.getInt("item_id");
    int quantity = itemRs.getInt("quantity");
    double unitCost = itemRs.getDouble("unit_cost");
    double amount = itemRs.getDouble("amount");
    
    report.append(" Item ID: ").append(itemId)
    .append(" | Qty: ").append(quantity)
    .append(" | Unit Cost: ").append(unitCost)
    .append(" | Amount: ").append(amount)
    .append("\n");
    }
    
    if (!hasItems) {
    report.append(" No item details found for this order.\n");
    }
    
    report.append("\n");
    }
    
    if (!hasOrders) {
    report.append("No activity found for this merchant in the selected period.\n");
    }
    
    } catch (SQLException e) {
    report.append("Error generating report: ").append(e.getMessage());
    }
    
    return report.toString();
    }


// generates stock turnover report (goods sold and newly received)
public String generateStockTurnoverReport(String startDate, String endDate) {
    StringBuilder report = new StringBuilder();

    report.append("=== Stock Turnover Report ===\n");
    report.append("From: ").append(startDate)
          .append(" To: ").append(endDate).append("\n\n");

    String sql = """
        SELECT
            SUM(CASE WHEN movement_type = 'OUT' THEN quantity ELSE 0 END) AS goods_sold,
            SUM(CASE WHEN movement_type = 'IN' THEN quantity ELSE 0 END) AS goods_received
        FROM stock_movements
        WHERE movement_date BETWEEN ? AND ?
    """;

    try {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, startDate);
        pstmt.setString(2, endDate);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int goodsSold = rs.getInt("goods_sold");
            int goodsReceived = rs.getInt("goods_received");

            report.append("Goods Sold: ").append(goodsSold).append("\n");
            report.append("Goods Received: ").append(goodsReceived).append("\n");
        } else {
            report.append("No stock movement data found for this period.\n");
        }

    } catch (SQLException e) {
        report.append("Error generating report: ").append(e.getMessage());
    }

    return report.toString();
}
// generates detailed report for a single invoice
public String generateInvoiceDetailsReport(String invoiceId) {
    StringBuilder report = new StringBuilder();

    report.append("=== Invoice Details ===\n");
    report.append("Invoice ID: ").append(invoiceId).append("\n\n");

    String sql = """
        SELECT invoice_id, merchant_id, amount, invoice_date, payment_status
        FROM invoices
        WHERE invoice_id = ?
    """;

    try {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, invoiceId);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            report.append("Merchant: ").append(rs.getString("merchant_id")).append("\n");
            report.append("Amount: ").append(rs.getDouble("amount")).append("\n");
            report.append("Date: ").append(rs.getString("invoice_date")).append("\n");
            report.append("Payment: ").append(rs.getString("payment_status")).append("\n");
        } else {
            report.append("Invoice not found.\n");
        }

    } catch (SQLException e) {
        report.append("Error generating report: ").append(e.getMessage());
    }

    return report.toString();
}
}