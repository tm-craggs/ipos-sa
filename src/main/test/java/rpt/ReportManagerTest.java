package rpt;

import db.DatabaseManager;
import db.DatabaseTestHelper;
import ord.OrderItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportManagerTest {

    private ReportManager reportManager;

    @BeforeEach
    void setUp() {
        DatabaseTestHelper.resetDatabase();
        reportManager = new ReportManager();
    }

    @AfterEach
    void tearDown() {
        DatabaseTestHelper.cleanup();
    }

    @Test
    void generateAllInvoicesReport_returnsNoInvoicesMessageWhenEmpty() {
        String result = reportManager.generateAllInvoicesReport("2026-01-01", "2026-12-31");

        assertTrue(result.contains("No invoices found for this period."));
    }

    @Test
    void generateAllInvoicesReport_includesInvoiceData() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "Painkiller", "Box", "Tablet", 16, 5.0, 100, 10, 10);

        int orderId = DatabaseManager.submitOrder("merchant1", "2026-04-19",
                10.0, List.of(new OrderItem(1, "Painkiller", 2, 5.0, 10.0)));

        String result = reportManager.generateAllInvoicesReport("2026-01-01", "2026-12-31");

        assertTrue(result.contains("INV-" + orderId));
        assertTrue(result.contains("merchant1"));
        assertTrue(result.contains("Pending"));
    }

    @Test
    void generateMerchantInvoicesReport_filtersByMerchant() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedMerchant("merchant2", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "Painkiller", "Box", "Tablet", 16, 5.0, 100, 10, 10);

        DatabaseManager.submitOrder("merchant1", "2026-04-19",
                10.0, List.of(new OrderItem(1, "Painkiller", 2, 5.0, 10.0)));
        DatabaseManager.submitOrder("merchant2", "2026-04-19",
                5.0, List.of(new OrderItem(1, "Painkiller", 1, 5.0, 5.0)));

        String result = reportManager.generateMerchantInvoicesReport("merchant1", "2026-01-01", "2026-12-31");

        assertTrue(result.contains("Merchant: merchant1"));
        assertTrue(result.contains("Invoice ID:"));
    }

    @Test
    void generateTurnoverReport_calculatesGoodsAndRevenue() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "ItemA", "Box", "Tablet", 10, 2.0, 100, 10, 10);

        DatabaseManager.submitOrder("merchant1", "2026-04-19",
                8.0, List.of(new OrderItem(1, "ItemA", 4, 2.0, 8.0)));

        String result = reportManager.generateTurnoverReport("2026-01-01", "2026-12-31");

        assertTrue(result.contains("Total Goods Sold: 4"));
        assertTrue(result.contains("Total Revenue: 8.0"));
    }

    @Test
    void generateMerchantOrdersReport_includesTotals() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "ItemA", "Box", "Tablet", 10, 2.5, 100, 10, 10);

        DatabaseManager.submitOrder("merchant1", "2026-04-19",
                5.0, List.of(new OrderItem(1, "ItemA", 2, 2.5, 5.0)));

        String result = reportManager.generateMerchantOrdersReport("merchant1", "2026-01-01", "2026-12-31");

        assertTrue(result.contains("=== Merchant Orders Report ==="));
        assertTrue(result.contains("Total Orders: 1"));
        assertTrue(result.contains("Total Value: 5.0"));
    }

    @Test
    void generateMerchantActivityReport_includesItemLines() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "Antacid", "Bottle", "ml", 1, 3.0, 100, 10, 10);

        DatabaseManager.submitOrder("merchant1", "2026-04-19",
                9.0, List.of(new OrderItem(1, "Antacid", 3, 3.0, 9.0)));

        String result = reportManager.generateMerchantActivityReport("merchant1", "2026-01-01", "2026-12-31");

        assertTrue(result.contains("Order ID:"));
        assertTrue(result.contains("Item ID: 1"));
        assertTrue(result.contains("Qty: 3"));
    }

    @Test
    void generateStockTurnoverReport_readsStockMovementsTable() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("INSERT INTO stock_movements (item_id, movement_type, quantity, movement_date) VALUES ('1', 'OUT', 5, '2026-04-10')");
            st.executeUpdate("INSERT INTO stock_movements (item_id, movement_type, quantity, movement_date) VALUES ('1', 'IN', 12, '2026-04-11')");
        }

        String result = reportManager.generateStockTurnoverReport("2026-01-01", "2026-12-31");

        assertTrue(result.contains("Goods Sold: 5"));
        assertTrue(result.contains("Goods Received: 12"));
    }

    @Test
    void generateInvoiceDetailsReport_returnsInvoiceDetailsWhenPresent() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "ItemA", "Box", "Tablet", 10, 2.0, 100, 10, 10);

        int orderId = DatabaseManager.submitOrder("merchant1", "2026-04-19",
                4.0, List.of(new OrderItem(1, "ItemA", 2, 2.0, 4.0)));

        String result = reportManager.generateInvoiceDetailsReport("INV-" + orderId);

        assertTrue(result.contains("=== Invoice Details ==="));
        assertTrue(result.contains("merchant1"));
        assertTrue(result.contains("4.0"));
    }

    @Test
    void generateInvoiceDetailsReport_returnsNotFoundMessageWhenMissing() {
        String result = reportManager.generateInvoiceDetailsReport("INV-99999");

        assertTrue(result.contains("Invoice not found."));
    }
}