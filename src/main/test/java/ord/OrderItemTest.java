package ord;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemTest {

    @Test
    void getters_returnExpectedValues() {
        OrderItem item = new OrderItem(1, "Vitamin C", 3, 2.5, 7.5);

        assertEquals(1, item.getItemId());
        assertEquals("Vitamin C", item.getDescription());
        assertEquals(3, item.getQuantity());
        assertEquals(2.5, item.getUnitCost(), 0.001);
        assertEquals(7.5, item.getAmount(), 0.001);
    }
}
