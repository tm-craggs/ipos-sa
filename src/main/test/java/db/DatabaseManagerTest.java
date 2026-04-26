package db;

import ord.Invoice;
import ord.OrderItem;
import ord.TrackedOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @BeforeEach
    void setUp() {
        DatabaseTestHelper.resetDatabase();
    }

    @AfterEach
    void tearDown() {
        DatabaseTestHelper.cleanup();
    }

    @Test
    void verifyUser_returnsRoleForValidCredentials() {
        DatabaseTestHelper.seedAdmin("alice");

        String result = DatabaseManager.verifyUser("alice", "pass123");

        assertEquals("Admin", result);
    }

    @Test
    void verifyUser_returnsNullForInvalidCredentials() {
        DatabaseTestHelper.seedAdmin("alice");

        String result = DatabaseManager.verifyUser("alice", "wrong");

        assertNull(result);
    }

    @Test
    void usernameExists_returnsTrueWhenUserExists() {
        DatabaseTestHelper.seedManager("bob");

        assertTrue(DatabaseManager.usernameExists("bob"));
    }

    @Test
    void usernameExists_returnsFalseWhenUserMissing() {
        assertFalse(DatabaseManager.usernameExists("missing"));
    }

    @Test
    void addCatalogueItem_andGetCatalogueItems_workCorrectly() {
        DatabaseTestHelper.seedCatalogueItem(1, "Paracetamol", "Box", "Tablet", 16, 2.50, 100, 20, 10);

        var items = DatabaseManager.getCatalogueItems();

        assertEquals(1, items.size());
        assertEquals("Paracetamol", items.get(0).getDescription());
        assertEquals(100, items.get(0).getAvailability());
    }

    @Test
    void getLowStockItems_returnsOnlyLowStockItems() {
        DatabaseTestHelper.seedCatalogueItem(1, "LowItem", "Box", "Tablet", 10, 1.0, 5, 10, 10);
        DatabaseTestHelper.seedCatalogueItem(2, "OkItem", "Bottle", "ml", 1, 3.0, 20, 10, 10);

        var lowStock = DatabaseManager.getLowStockItems();

        assertEquals(1, lowStock.size());
        assertEquals(1, lowStock.get(0).getItemId());
        assertEquals("LowItem", lowStock.get(0).getDescription());
    }

    @Test
    void updateAvailability_changesStatusToLowStockWhenBelowLimit() {
        DatabaseTestHelper.seedCatalogueItem(1, "Ibuprofen", "Box", "Tablet", 16, 4.99, 50, 20, 10);

        DatabaseManager.updateAvailability(1, 5);

        var items = DatabaseManager.getCatalogueItems();
        assertEquals(1, items.size());
        assertEquals("Low stock", items.get(0).getStatus());
        assertEquals(5, items.get(0).getAvailability());
    }

    @Test
    void getCreditLimit_returnsMerchantCreditLimit() {
        DatabaseTestHelper.seedMerchant("merchant1", 500.0f, "fixed");

        double limit = DatabaseManager.getCreditLimit("merchant1");

        assertEquals(500.0, limit, 0.001);
    }

    @Test
    void submitOrder_createsOrderItemsAndInvoice_andReducesStock() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "Vitamin C", "Box", "Tablet", 20, 5.00, 50, 10, 10);

        List<OrderItem> items = List.of(
                new OrderItem(1, "Vitamin C", 3, 5.00, 15.00)
        );

        int orderId = DatabaseManager.submitOrder("merchant1", "2026-04-19", 15.00, items);

        assertTrue(orderId > 0);

        Optional<TrackedOrder> order = DatabaseManager.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals("merchant1", order.get().getMerchantId());
        assertEquals(15.00, order.get().getOrderValue(), 0.001);

        List<OrderItem> savedItems = DatabaseManager.getOrderItems(String.valueOf(orderId));
        assertEquals(1, savedItems.size());
        assertEquals(3, savedItems.get(0).getQuantity());

        List<Invoice> unpaid = DatabaseManager.getUnpaidInvoices("merchant1");
        assertEquals(1, unpaid.size());
        assertEquals("INV-" + orderId, unpaid.get(0).getInvoiceId());

        var catalogue = DatabaseManager.getCatalogueItems();
        assertEquals(47, catalogue.get(0).getAvailability());
    }

    @Test
    void getOrdersByMerchant_returnsOrdersForThatMerchantOnly() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedMerchant("merchant2", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "ItemA", "Box", "Tablet", 10, 2.0, 100, 10, 10);

        DatabaseManager.submitOrder("merchant1", "2026-04-18",
                4.0, List.of(new OrderItem(1, "ItemA", 2, 2.0, 4.0)));

        DatabaseManager.submitOrder("merchant2", "2026-04-18",
                6.0, List.of(new OrderItem(1, "ItemA", 3, 2.0, 6.0)));

        List<TrackedOrder> merchant1Orders = DatabaseManager.getOrdersByMerchant("merchant1");

        assertEquals(1, merchant1Orders.size());
        assertEquals("merchant1", merchant1Orders.get(0).getMerchantId());
    }

    @Test
    void markInvoicePaid_removesInvoiceFromUnpaidList() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "ItemA", "Box", "Tablet", 10, 2.0, 100, 10, 10);

        int orderId = DatabaseManager.submitOrder("merchant1", "2026-04-18",
                4.0, List.of(new OrderItem(1, "ItemA", 2, 2.0, 4.0)));

        DatabaseManager.markInvoicePaid("INV-" + orderId);

        List<Invoice> unpaid = DatabaseManager.getUnpaidInvoices("merchant1");
        assertTrue(unpaid.isEmpty());
    }

    @Test
    void getOutstandingBalance_returnsSumOfUnpaidInvoices() {
        DatabaseTestHelper.seedMerchant("merchant1", 1000.0f, "fixed");
        DatabaseTestHelper.seedCatalogueItem(1, "ItemA", "Box", "Tablet", 10, 2.0, 100, 10, 10);

        DatabaseManager.submitOrder("merchant1", "2026-04-18",
                4.0, List.of(new OrderItem(1, "ItemA", 2, 2.0, 4.0)));
        DatabaseManager.submitOrder("merchant1", "2026-04-19",
                6.0, List.of(new OrderItem(1, "ItemA", 3, 2.0, 6.0)));

        double outstanding = DatabaseManager.getOutstandingBalance("merchant1");

        assertEquals(10.0, outstanding, 0.001);
    }

    @Test
    void getCatalogueItem_returnsItemWhenPresent() {
        DatabaseTestHelper.seedCatalogueItem(99, "Zinc", "Box", "Tablet", 30, 4.50, 60, 10, 10);

        var item = DatabaseManager.getCatalogueItem(99);

        assertTrue(item.isPresent());
        assertEquals("Zinc", item.get().getDescription());
    }
}