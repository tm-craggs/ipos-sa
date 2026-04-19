package ord;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvoiceTest {

    @Test
    void constructor_usesPendingWhenStatusIsNull() {
        Invoice invoice = new Invoice("INV-1", "2026-04-19", 12.0, null);

        assertEquals("INV-1", invoice.getInvoiceId());
        assertEquals("2026-04-19", invoice.getInvoiceDate());
        assertEquals(12.0, invoice.getAmount(), 0.001);
        assertEquals("Pending", invoice.getStatus());
    }

    @Test
    void constructor_keepsProvidedStatus() {
        Invoice invoice = new Invoice("INV-2", "2026-04-19", 8.0, "Paid");

        assertEquals("Paid", invoice.getStatus());
    }
}
