package cat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StockLowLevelTest {

    @Test
    void constructor_calculatesRecommendedUsingOrderPercentage() {
        StockLowLevel item = new StockLowLevel(1, "ItemA", 10, 20, 10);

        assertEquals(1, item.getItemId());
        assertEquals("ItemA", item.getDescription());
        assertEquals(10, item.getAvailability());
        assertEquals(20, item.getStockLimit());
        assertEquals(1.1, item.getOrderPercentage(), 0.001);
        assertEquals(12, item.getRecommended());
    }

    @Test
    void secondConstructor_usesDefaultPercentageValue() {
        StockLowLevel item = new StockLowLevel(2, "ItemB", 5, 20);

        assertEquals(2, item.getItemId());
        assertEquals("ItemB", item.getDescription());
        assertTrue(item.getRecommended() > 0);
    }
}
