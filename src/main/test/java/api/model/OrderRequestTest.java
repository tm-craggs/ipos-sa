package api.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderRequestTest {

    @Test
    void gettersAndSetters_workCorrectly() {
        ItemRequest item = new ItemRequest();
        item.setItemId(1);
        item.setQuantity(3);

        OrderRequest request = new OrderRequest();
        request.setMerchantId("merchant1");
        request.setItems(List.of(item));

        assertEquals("merchant1", request.getMerchantId());
        assertEquals(1, request.getItems().size());
        assertEquals(1, request.getItems().get(0).getItemId());
        assertEquals(3, request.getItems().get(0).getQuantity());
    }
}
