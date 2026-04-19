package acc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAccountTest {

    @Test
    void constructor_usesDashWhenStatusIsNull() {
        UserAccount account = new UserAccount(1, "merchant1", "Merchant", null);

        assertEquals("—", account.getStatus());
    }

    @Test
    void getters_returnExpectedValues() {
        UserAccount account = new UserAccount(2, "admin1", "Admin", "Normal");

        assertEquals(2, account.getId());
        assertEquals("admin1", account.getUsername());
        assertEquals("Admin", account.getType());
        assertEquals("Normal", account.getStatus());
    }
}
