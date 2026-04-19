package api.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        ItemRequest request = new ItemRequest();
        request.setItemId(5);
        request.setQuantity(12);

        assertEquals(5, request.getItemId());
        assertEquals(12, request.getQuantity());
    }
}
