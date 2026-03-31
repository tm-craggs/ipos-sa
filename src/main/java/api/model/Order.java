package api.model;

public class Order {

    final private int orderId;
    final private int itemId;
    private String orderStatus;

    public Order(int orderId, int itemId, String orderStatus) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.orderStatus = orderStatus;
    }

    public int getId() {
        return orderId;
    }
    public int getItemId() {
        return itemId;
    }
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

}