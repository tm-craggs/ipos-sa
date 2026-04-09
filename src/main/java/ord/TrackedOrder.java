package ord;

import javafx.beans.property.*;

public class TrackedOrder {
    private final StringProperty orderId = new SimpleStringProperty();
    private final StringProperty merchantId = new SimpleStringProperty();
    private final StringProperty orderDate = new SimpleStringProperty();
    private final DoubleProperty orderValue = new SimpleDoubleProperty();
    private final StringProperty dispatchDate = new SimpleStringProperty();
    private final StringProperty deliveredDate = new SimpleStringProperty();
    private final StringProperty paymentStatus = new SimpleStringProperty();

    public TrackedOrder(String orderId, String merchantId, String orderDate,
                        double orderValue, String dispatchDate,
                        String deliveredDate, String paymentStatus) {
        this.orderId.set(orderId);
        this.merchantId.set(merchantId);
        this.orderDate.set(orderDate);
        this.orderValue.set(orderValue);
        this.dispatchDate.set(dispatchDate != null ? dispatchDate : "Pending");
        this.deliveredDate.set(deliveredDate != null ? deliveredDate : "Pending");
        this.paymentStatus.set(paymentStatus != null ? paymentStatus : "Pending");
    }

    public StringProperty orderIdProperty() { return orderId; }
    public StringProperty merchantIdProperty() { return merchantId; }
    public StringProperty orderDateProperty() { return orderDate; }
    public DoubleProperty orderValueProperty() { return orderValue; }
    public StringProperty dispatchDateProperty() { return dispatchDate; }
    public StringProperty deliveredDateProperty() { return deliveredDate; }
    public StringProperty paymentStatusProperty() { return paymentStatus; }

    public String getOrderId() { return orderId.get(); }
    public String getMerchantId() { return merchantId.get(); }
    public String getOrderDate() { return orderDate.get(); }
    public double getOrderValue() { return orderValue.get(); }
    public String getDispatchDate() { return dispatchDate.get(); }
    public String getDeliveredDate() { return deliveredDate.get(); }
    public String getPaymentStatus() { return paymentStatus.get(); }
}