package cat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CatalogueItemTest {

    @Test
    void constructor_setsLowStockStatusWhenAvailabilityBelowLimit() {
        CatalogueItem item = new CatalogueItem(1, "Paracetamol", "Box", "Tablet", 16, 2.5, 5, 10, 10.0);

        assertEquals("Low stock", item.getStatus());
    }

    @Test
    void constructor_setsOkStatusWhenAvailabilityAtOrAboveLimit() {
        CatalogueItem item = new CatalogueItem(1, "Paracetamol", "Box", "Tablet", 16, 2.5, 20, 10, 10.0);

        assertEquals("OK", item.getStatus());
    }

    @Test
    void getters_returnCorrectValues() {
        CatalogueItem item = new CatalogueItem(2, "Ibuprofen", "Bottle", "ml", 1, 4.99, 12, 5, 15.0);

        assertEquals(2, item.getItemId());
        assertEquals("Ibuprofen", item.getDescription());
        assertEquals("Bottle", item.getPackageType());
        assertEquals("ml", item.getUnit());
        assertEquals(1, item.getUnitsPerPack());
        assertEquals(4.99, item.getPackageCost(), 0.001);
        assertEquals(12, item.getAvailability());
        assertEquals(5, item.getStockLimit());
        assertEquals(15.0, item.getOrderPercentage(), 0.001);
    }
}
