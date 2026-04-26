package ipos.sa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @BeforeEach
    void resetSession() {
        UserSession.logout();
    }

    @Test
    void login_createsSession() {
        UserSession.login("merchant1", "Merchant");

        assertNotNull(UserSession.getInstance());
        assertEquals("merchant1", UserSession.getInstance().getUsername());
        assertEquals("Merchant", UserSession.getInstance().getType());
    }

    @Test
    void login_doesNotReplaceExistingSession() {
        UserSession.login("firstUser", "Admin");
        UserSession.login("secondUser", "Merchant");

        assertEquals("firstUser", UserSession.getInstance().getUsername());
        assertEquals("Admin", UserSession.getInstance().getType());
    }

    @Test
    void logout_clearsSession() {
        UserSession.login("merchant1", "Merchant");

        UserSession.logout();

        assertNull(UserSession.getInstance());
    }
}
