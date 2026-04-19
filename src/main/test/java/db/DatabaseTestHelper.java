package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseTestHelper {

    private DatabaseTestHelper() {}

    public static void resetDatabase() {
        DatabaseManager.setTestDatabase("ipos-sa-test.db");
        DatabaseManager.connect();

        try {
            Connection conn = DatabaseManager.getConnection();
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("DELETE FROM order_items");
                st.executeUpdate("DELETE FROM invoices");
                st.executeUpdate("DELETE FROM orders");
                st.executeUpdate("DELETE FROM stock_movements");
                st.executeUpdate("DELETE FROM commercial_applications");
                st.executeUpdate("DELETE FROM catalogue");
                st.executeUpdate("DELETE FROM users WHERE username != 'director'");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset test database", e);
        }
    }

    public static void seedMerchant(String username, float creditLimit, String discountPlan) {
        DatabaseManager.addUser(username, "pass123", "Merchant", creditLimit, discountPlan);
    }

    public static void seedAdmin(String username) {
        DatabaseManager.addUser(username, "pass123", "Admin");
    }

    public static void seedManager(String username) {
        DatabaseManager.addUser(username, "pass123", "Manager");
    }

    public static void seedCatalogueItem(
            int itemId,
            String description,
            String packageType,
            String unit,
            int unitsPerPack,
            double packageCost,
            int availability,
            int stockLimit,
            double orderPercentage
    ) {
        DatabaseManager.addCatalogueItem(
                itemId, description, packageType, unit,
                unitsPerPack, packageCost, availability, stockLimit, orderPercentage
        );
    }

    public static void cleanup() {
        DatabaseManager.disconnect();
        DatabaseManager.resetToProductionDatabase();
    }
}