package ipos.sa;

public class UserSession {
    private static UserSession instance;

    private final String username;
    private final String type; // merchant, admin, etc.

    // this constructor is private, as only one user session can exist at a time
    private UserSession(String username, String type) {
        this.username = username;
        this.type = type;
    }

    // this method calls constructor to create UserSession
    // note: this is separate from the constructor to ensure only one user can exist at once
    public static void login(String username, String type) {
        if (instance == null) {
            instance = new UserSession(username, type);
            System.out.println("Login successful!");
            System.out.println("Logged in as: " + username);
            System.out.println("Account type: " + type);
        } else {
            System.out.println("Error: User already logged in");
        }
    }

    public static void logout() {
        instance = null;
    }

    public String getUsername() { return username; }
    public String getType() { return type; }

}