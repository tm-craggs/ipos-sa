package db;

import acc.UserAccount;
import api.model.CommercialApplication;
import cat.CatalogueItem;
import cat.StockLowLevel;
import ord.Invoice;
import ord.OrderItem;
import ord.TrackedOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:ipos-sa.db";
    private static String currentUrl = URL;
    private static Connection conn;

    public static void connect() {
        try {
            if (conn != null && !conn.isClosed()) {
                return;
            }

            conn = DriverManager.getConnection(currentUrl);
            System.out.println("Connection to SQLite has been established.");
            init();
        } catch (SQLException e) {
            System.out.println("Failed to connect: " + e.getMessage());
        }
    }

    public static void setTestDatabase(String dbFileName) {
        disconnect();
        currentUrl = "jdbc:sqlite:" + dbFileName;
    }

    public static void resetToProductionDatabase() {
        disconnect();
        currentUrl = URL;
    }

    public static void disconnect() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection: " + e.getMessage());
            } finally {
                conn = null;
            }
        }
    }


    private static void init() throws SQLException {

        // run queries to set up basic tables
        try (var st = conn.createStatement()) {

            // create user table for ipos-sa-acc
            st.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL,
                            type TEXT NULL,
                            credit_limit REAL,
                            discount_plan TEXT,
                            status TEXT
                        );
                    """);

            // seed first user into users database
            st.execute("""
                        INSERT OR IGNORE INTO users (username, password, type)
                        VALUES ('director', 'director', 'Director')
                    """);

            // adding a database for table for ipos-sa-cat
            st.execute("""
                        CREATE TABLE IF NOT EXISTS catalogue (
                            item_id INTEGER PRIMARY KEY,
                            description TEXT NOT NULL,
                            package_type TEXT,
                            unit TEXT,
                            units_per_pack INTEGER,
                            package_cost REAL,
                            availability INTEGER,
                            stock_limit INTEGER,
                            status TEXT,
                            order_percentage DOUBLE
                        );
                    """);

            // adding database tables for ipos-sa-rpt
            st.execute("""
                        CREATE TABLE IF NOT EXISTS invoices (
                            invoice_id TEXT PRIMARY KEY,
                            merchant_id TEXT NOT NULL,
                            amount REAL NOT NULL,
                            invoice_date TEXT NOT NULL,
                            payment_status TEXT
                        );
                    """);

            st.execute("""
                        CREATE TABLE IF NOT EXISTS orders (
                            order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            merchant_id TEXT NOT NULL,
                            order_date TEXT NOT NULL,
                            order_value REAL NOT NULL,
                            dispatch_date TEXT,
                            delivered_date TEXT,
                            payment_status TEXT,
                            delivery_status TEXT
                        );
                    """);

            st.execute("""
                        CREATE TABLE IF NOT EXISTS stock_movements (
                            movement_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            item_id TEXT NOT NULL,
                            movement_type TEXT NOT NULL,
                            quantity INTEGER NOT NULL,
                            movement_date TEXT NOT NULL
                        );
                    """);
            st.execute("""
                        CREATE TABLE IF NOT EXISTS order_items (
                            order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            order_id TEXT NOT NULL,
                            item_id INTEGER NOT NULL,
                            quantity INTEGER NOT NULL,
                            unit_cost REAL NOT NULL,
                            amount REAL NOT NULL
                        );
                    """);

            st.execute("""
                        CREATE TABLE IF NOT EXISTS commercial_applications (
                            application_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            company_name TEXT NOT NULL,
                            reg_num INTEGER NOT NULL,
                            email TEXT NOT NULL,
                            phone TEXT NOT NULL,
                            address TEXT NOT NULL,
                            director TEXT NOT NULL
                        );
                    """);
        }
    }

    public static String verifyUser(String username, String password) {
        String sql = "SELECT type FROM users WHERE username = ? AND password = ?";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString("type") : null;
        } catch (SQLException e) {
            return null;
        }
    }

    public static void addUser(String username, String password, String type) {
        String sql = "INSERT INTO users (username, password, type) VALUES (?, ?, ?)";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // add merchant routine
    public static void addUser(String username, String password, String type, float creditLimit, String discountPlan) {
        String sql = "INSERT INTO users (username, password, type, credit_limit, discount_plan, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, type);
            pstmt.setFloat(4, creditLimit);
            pstmt.setString(5, discountPlan);
            pstmt.setString(6, "Normal");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding full user: " + e.getMessage());
        }
    }


    public static List<UserAccount> getUsers(String callerRole) {
        List<UserAccount> list = new ArrayList<>();
        String sql = switch (callerRole) {
            case "Director" -> "SELECT id, username, type, status FROM users WHERE username != 'director'";
            case "Admin" -> "SELECT id, username, type, status FROM users WHERE type IN ('Manager', 'Merchant')";
            case "Manager" -> "SELECT id, username, type, status FROM users WHERE type = 'Merchant'";
            default -> null;
        };

        if (sql == null) return list; // Merchants and unknowns get nothing

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new UserAccount(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("type"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to load users: " + e.getMessage());
        }
        return list;
    }

    public static void setUserStatus(int id, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update status: " + e.getMessage());
        }
    }

    public static void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete user: " + e.getMessage());
        }
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("Failed to check username: " + e.getMessage());
            return false;
        }
    }

    public static List<StockLowLevel> getLowStockItems() {
        List<StockLowLevel> list = new ArrayList<>();
        String sql = "SELECT * FROM catalogue WHERE status = 'Low stock';";
        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new StockLowLevel(
                        rs.getInt("item_id"),
                        rs.getString("description"),
                        rs.getInt("availability"),
                        rs.getInt("stock_limit"),
                        rs.getDouble("order_percentage")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to load catalogue: " + e.getMessage());
        }
        return list;
    }

    public static List<CatalogueItem> getCatalogueItems() {
        List<CatalogueItem> items = new ArrayList<>();
        String sql = "SELECT * FROM catalogue";
        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(new CatalogueItem(
                        rs.getInt("item_id"),
                        rs.getString("description"),
                        rs.getString("package_type"),
                        rs.getString("unit"),
                        rs.getInt("units_per_pack"),
                        rs.getDouble("package_cost"),
                        rs.getInt("availability"),
                        rs.getInt("stock_limit"),
                        rs.getDouble("order_percentage")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to load catalogue: " + e.getMessage());
        }
        return items;
    }


    public static void addCatalogueItem(int itemId, String Description, String packageType, String unit, int unitsperPack, double packageCost, int availability, int stockLimit, double orderPercentage) {
        String statusValue = availability < stockLimit ? "Low stock" : "OK";
        String sql = """
                    INSERT INTO catalogue (item_id, description, package_type, unit, units_per_pack, package_cost, availability, stock_limit, status, order_percentage)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.setString(2, Description);
            pstmt.setString(3, packageType);
            pstmt.setString(4, unit);
            pstmt.setInt(5, unitsperPack);
            pstmt.setDouble(6, packageCost);
            pstmt.setInt(7, availability);
            pstmt.setInt(8, stockLimit);
            pstmt.setString(9, statusValue);
            pstmt.setDouble(10, orderPercentage);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add item: " + e.getMessage());
        }
    }

    private static void updateStatus(int id) {
        String sql = """
                UPDATE catalogue SET status = CASE WHEN availability < stock_limit THEN 'Low stock' ELSE 'OK' END WHERE item_id = ?
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update status: " + e.getMessage());
        }
    }

    public static void updateDescription(int id, String description) {
        String sql = """
                UPDATE catalogue SET description = ? WHERE item_id = ?
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update description: " + e.getMessage());
        }
    }

    public static void updatePackageType(int id, String packageType) {
        String sql = """ 
                UPDATE catalogue SET package_type = ? WHERE item_id = ?
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, packageType);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update description: " + e.getMessage());
        }
    }

    public static void updateUnit(int id, String unit) {
        String sql = """ 
                UPDATE catalogue SET unit = ? WHERE item_id = ?
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, unit);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update description: " + e.getMessage());
        }
    }

    public static void updateUnitsPerPack(int id, int unitsPerPack) {
        String sql = """ 
                UPDATE catalogue SET units_per_pack = ? WHERE item_id = ?
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, unitsPerPack);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update description: " + e.getMessage());
        }
    }

    public static void updatePackageCost(int id, double packageCost) {
        String sql = """ 
                UPDATE catalogue SET package_cost = ? WHERE item_id = ?
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, packageCost);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update description: " + e.getMessage());
        }
    }

    public static void updateAvailability(int id, int availability) {
        String sql = """ 
                UPDATE catalogue SET availability = ? WHERE item_id = ?
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, availability);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            updateStatus(id);
        } catch (SQLException e) {
            System.out.println("Failed to update description: " + e.getMessage());
        }
    }

    public static void updateStockLimit(int id, int stockLimit) {
        String sql = """ 
                UPDATE catalogue SET stock_limit = ? WHERE item_id = ?
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stockLimit);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            updateStatus(id);
        } catch (SQLException e) {
            System.out.println("Failed to update description: " + e.getMessage());
        }
    }

    public static void deleteCatalogueItem(int id) {
        String sql = """
                DELETE FROM catalogue WHERE item_id = ?""";
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete item: " + e.getMessage());
        }
    }

    public static void updateOrderPerentage(int id, double OP) {
        String sql = """ 
                UPDATE catalogue SET order_percentage = ? WHERE item_id = ?
                """;
        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, OP);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            updateStatus(id);
        } catch (SQLException e) {
            System.out.println("Failed to update description: " + e.getMessage());

        }
    }

    // table for order the order id and merchantid are as strings idk why but someone should fix it

    // tried to refactor this function to not take in orderID as a param and generate it, lmk if it messes stuff up
    public static int submitOrder(String merchantId, String orderDate, double orderValue, List<OrderItem> items) {
        try {
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO orders (merchant_id, order_date, order_value, payment_status, delivery_status) VALUES (?,?,?,?,?)";

            var ps1 = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, merchantId);
            ps1.setString(2, orderDate);
            ps1.setDouble(3, orderValue);
            ps1.setString(4, "Pending");
            ps1.setString(5, "Awaiting dispatch");
            ps1.executeUpdate();

            int orderId = -1;
            var keys = ps1.getGeneratedKeys();
            if (keys.next()) {
                orderId = keys.getInt(1);
            }
            ps1.close();

            var ps2 = conn.prepareStatement("INSERT INTO order_items (order_id, item_id, quantity, unit_cost, amount) VALUES (?,?,?,?,?)");
            var ps3 = conn.prepareStatement("UPDATE catalogue SET availability = availability - ? WHERE item_id = ?");

            for (OrderItem item : items) {
                ps2.setInt(1, orderId);
                ps2.setInt(2, item.getItemId());
                ps2.setInt(3, item.getQuantity());
                ps2.setDouble(4, item.getUnitCost());
                ps2.setDouble(5, item.getAmount());
                ps2.executeUpdate();

                ps3.setInt(1, item.getQuantity());
                ps3.setInt(2, item.getItemId());
                ps3.executeUpdate();
            }
            ps2.close();
            ps3.close();


            var ps4 = conn.prepareStatement("INSERT INTO invoices (invoice_id, merchant_id, amount, invoice_date, payment_status) VALUES (?,?,?,?,?)");
            ps4.setString(1, "INV-" + orderId);
            ps4.setString(2, merchantId);
            ps4.setDouble(3, orderValue);
            ps4.setString(4, orderDate);
            ps4.setString(5, "Pending");
            ps4.executeUpdate();
            ps4.close();

            conn.commit();

            return orderId;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static List<TrackedOrder> getOrdersByMerchant(String merchantId) {
        List<TrackedOrder> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE merchant_id = ? ORDER BY order_date DESC";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, merchantId);
            var rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new TrackedOrder(
                        rs.getString("order_id"),
                        rs.getString("merchant_id"),
                        rs.getString("order_date"),
                        rs.getDouble("order_value"),
                        rs.getString("dispatch_date"),
                        rs.getString("delivered_date"),
                        rs.getString("payment_status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to load orders: " + e.getMessage());
        }
        return list;
    }

    public static List<OrderItem> getOrderItems(String orderId) {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.item_id, c.description, oi.quantity, oi.unit_cost, oi.amount FROM order_items oi LEFT JOIN catalogue c ON oi.item_id = c.item_id WHERE oi.order_id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderId);
            var rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new OrderItem(
                        rs.getInt("item_id"),
                        rs.getString("description") != null ? rs.getString("description") : "Unknown",
                        rs.getInt("quantity"),
                        rs.getDouble("unit_cost"),
                        rs.getDouble("amount")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to load order items: " + e.getMessage());
        }
        return list;
    }

    public static Optional<TrackedOrder> getOrder(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new TrackedOrder(
                        rs.getString("order_id"),
                        rs.getString("merchant_id"),
                        rs.getString("order_date"),
                        rs.getDouble("order_value"),
                        rs.getString("dispatch_date"),
                        rs.getString("delivered_date"),
                        rs.getString("payment_status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get order: " + e.getMessage());
        }
        return Optional.empty();
    }

    public static List<Invoice> getUnpaidInvoices(String merchantId) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE merchant_id = ? AND payment_status != 'Paid' ORDER BY invoice_date DESC";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, merchantId);
            var rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Invoice(
                        rs.getString("invoice_id"),
                        rs.getString("invoice_date"),
                        rs.getDouble("amount"),
                        rs.getString("payment_status")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to load invoices: " + e.getMessage());
        }
        return list;
    }

    public static void markInvoicePaid(String invoiceId) {
        String sql = "UPDATE invoices SET payment_status = 'Paid' WHERE invoice_id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update invoice: " + e.getMessage());
        }
    }

    public static double getCreditLimit(String username) {
        String sql = "SELECT credit_limit FROM users WHERE username = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("credit_limit");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDiscountPlan(String username) {
        String sql = "SELECT discount_plan FROM users WHERE username = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getString("discount_plan");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "fixed";
    }

    public static void updateCreditLimit(int id, float limit) {
        String sql = "UPDATE users SET credit_limit = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, limit);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDiscountPlan(int id, String plan) {
        String sql = "UPDATE users SET discount_plan = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, plan);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUserType(int id, String type) {
        String sql = "UPDATE users SET type = ? WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double getOutstandingBalance(String merchantId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM invoices WHERE merchant_id = ? AND payment_status != 'Paid'";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, merchantId);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Optional<CatalogueItem> getCatalogueItem(int itemId) {
        String sql = "SELECT * FROM catalogue WHERE item_id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new CatalogueItem(
                        rs.getInt("item_id"),
                        rs.getString("description"),
                        rs.getString("package_type"),
                        rs.getString("unit"),
                        rs.getInt("units_per_pack"),
                        rs.getDouble("package_cost"),
                        rs.getInt("availability"),
                        rs.getInt("stock_limit"),
                        rs.getDouble("order_percentage")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get item: " + e.getMessage());
        }
        return Optional.empty();
    }


    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
        return conn;
    }

    public static void main(String[] args) {
        connect();
    }

    public static void clearMerchantFields(int id) {

        String sql = "UPDATE users SET credit_limit = NULL, discount_plan = NULL, status = NULL WHERE id = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to clear merchant fields: " + e.getMessage());
        }

    }

    public static boolean saveApplication(CommercialApplication application) {
        String sql = "INSERT INTO commercial_applications (company_name, reg_num, email, phone, address, director) VALUES (?, ?, ?, ?, ?, ?)";
        try (var pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, application.getCompany_name());
            pstmt.setInt(2, application.getReg_num());
            pstmt.setString(3, application.getEmail());
            pstmt.setString(4, application.getPhone());
            pstmt.setString(5, application.getAddress());
            pstmt.setString(6, application.getDirector());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static List<CommercialApplication> getApplications() {
        List<CommercialApplication> apps = new ArrayList<>();
        String sql = "SELECT * FROM commercial_applications";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                apps.add(new CommercialApplication(
                        rs.getInt("application_id"),
                        rs.getString("company_name"),
                        rs.getInt("reg_num"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("director")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return apps;
    }

    public static void deleteApplication(int id) {
        String sql = "DELETE FROM commercial_applications WHERE application_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}