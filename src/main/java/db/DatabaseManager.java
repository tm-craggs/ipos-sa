package db;

import cat.CatalogueItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                    status TEXT
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
                        rs.getInt("stock_limit")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to load catalogue: " + e.getMessage());
        }
        return items;
    }


    public static void addCatalogueItem(int itemId,String Description, String packageType, String unit, int unitsperPack, double packageCost,int availability, int stockLimit) {
        String statusValue = availability < stockLimit ? "Low stock" : "OK";
        String sql ="""
            INSERT INTO catalogue (item_id, description, package_type, unit, units_per_pack, package_cost, availability, stock_limit, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
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
            pstmt.executeUpdate();
            System.out.println("Item added to catalogue: " + itemId);
        } catch (SQLException e) {
            System.out.println("Failed to add item: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        connect();
    }
}