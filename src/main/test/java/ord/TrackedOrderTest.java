package ord;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrackedOrderTest {

    @Test
    void constructor_usesPendingForNullDatesAndStatus() {
        TrackedOrder order = new TrackedOrder("1", "merchant1", "2026-04-19", 25.0, null, null, null);

        assertEquals("Pending", order.getDispatchDate());
        assertEquals("Pending", order.getDeliveredDate());
        assertEquals("Pending", order.getPaymentStatus());
    }

    @Test
    void getters_returnExpectedValues() {
        TrackedOrder order = new TrackedOrder("2", "merchant2", "2026-04-20", 30.0, "2026-04-21", "2026-04-22", "Paid");

        assertEquals("2", order.getOrderId());
        assertEquals("merchant2", order.getMerchantId());
        assertEquals("2026-04-20", order.getOrderDate());
        assertEquals(30.0, order.getOrderValue(), 0.001);
        assertEquals("2026-04-21", order.getDispatchDate());
        assertEquals("2026-04-22", order.getDeliveredDate());
        assertEquals("Paid", order.getPaymentStatus());
    }
}
