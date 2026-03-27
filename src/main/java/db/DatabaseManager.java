package db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:ipos-sa.db";
    private static Connection conn;

    public static void connect() {
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite has been established.");
            init();
        } catch (SQLException e) {
            System.out.println("Failed to connect: " + e.getMessage());
        }
    }

    /**
     * This is the init function. This function is called once the connection has been established. It will check
     * that the users database has been created, and will create it if not. It also makes sure the director account is
     * created.
     * @throws SQLException
     */
    private static void init() throws SQLException {
        try (var st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    type TEXT NULL
                );
            """);
            st.execute("""
                INSERT OR IGNORE INTO users (username, password, type)
                VALUES ('director', 'director', 'DIRECTOR')
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
        order_id TEXT PRIMARY KEY,
        merchant_id TEXT NOT NULL,
        order_date TEXT NOT NULL,
        order_value REAL NOT NULL,
        dispatch_date TEXT,
        payment_status TEXT
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
    INSERT OR IGNORE INTO stock_movements (movement_id, item_id, movement_type, quantity, movement_date)
    VALUES
    (1, 'ITM001', 'SOLD', 20, '2026-01-05'),
    (2, 'ITM001', 'RECEIVED', 50, '2026-01-10'),
    (3, 'ITM002', 'SOLD', 10, '2026-01-12'),
    (4, 'ITM002', 'RECEIVED', 30, '2026-01-15');
""");

st.execute("""
    INSERT OR IGNORE INTO invoices (invoice_id, merchant_id, amount, invoice_date, payment_status)
    VALUES 
    ('INV001', 'M001', 100, '2026-01-01', 'PAID'),
    ('INV002', 'M002', 200, '2026-01-05', 'PENDING');
""");

st.execute("""
    INSERT OR IGNORE INTO orders (order_id, merchant_id, order_date, order_value, dispatch_date, payment_status)
    VALUES
    ('ORD001', 'M001', '2026-01-01', 100, '2026-01-02', 'PAID'),
    ('ORD002', 'M001', '2026-01-05', 200, '2026-01-06', 'PENDING'),
    ('ORD003', 'M002', '2026-01-07', 150, '2026-01-08', 'PAID');
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

    public static void main(String[] args) {
        connect();
    }
    public static Connection getConnection() {
        return conn;
    }
}