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
                    type TEXT NULL,
                    credit_limit REAL,
                    discount_plan TEXT
                );
            """);
            st.execute("""
                INSERT OR IGNORE INTO users (username, password, type)
                VALUES ('director', 'director', 'DIRECTOR')
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

    public static void addUser(String type, String username, String password){
        String sql = "INSERT INTO users (type, username, password) VALUES (?, ?, ?)";
        try (var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            pstmt.setString(2, username);
            pstmt.setString(3, password);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(String type, String username, String password, float credit_limit, String discount_plan){
        String sql = "INSERT INTO users (type, username, password, credit_limit, discount_plan) VALUES (?, ?, ?, ?, ?)";
        try (var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setFloat(4, credit_limit);
            pstmt.setString(5, discount_plan);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        connect();
    }
}