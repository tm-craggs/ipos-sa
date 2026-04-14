package api.model;

public class Order {

    private final int orderId;
    private final String merchantId;
    private final String orderDate;
    private final double orderValue;
    private final String paymentStatus;
    private final String dispatchStatus;

    public Order(int orderId, String merchantId, String orderDate, double orderValue, String paymentStatus, String dispatchStatus) {
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.orderDate = orderDate;
        this.orderValue = orderValue;
        this.paymentStatus = paymentStatus;
        this.dispatchStatus = dispatchStatus;
    }

    public int getOrderId()          { return orderId; }
    public String getMerchantId()    { return merchantId; }
    public String getOrderDate()     { return orderDate; }
    public double getOrderValue()    { return orderValue; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getDispatchStatus(){ return dispatchStatus; }

}