package api.model;
import java.util.List;


import java.util.List;

public class OrderRequest {
    private String merchantId;
    private List<ItemRequest> items;

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public List<ItemRequest> getItems() { return items; }
    public void setItems(List<ItemRequest> items) { this.items = items; }
}