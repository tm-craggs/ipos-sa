package rpt;

import javafx.beans.property.*;

public class InvoiceRecord {
    private final StringProperty invoiceId = new SimpleStringProperty();
    private final StringProperty merchantId = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final StringProperty invoiceDate = new SimpleStringProperty();
    private final StringProperty paymentStatus = new SimpleStringProperty();

    public InvoiceRecord(String invoiceId, String merchantId, double amount, String invoiceDate, String paymentStatus) {
        this.invoiceId.set(invoiceId);
        this.merchantId.set(merchantId);
        this.amount.set(amount);
        this.invoiceDate.set(invoiceDate);
        this.paymentStatus.set(paymentStatus);
    }

    public StringProperty invoiceIdProperty() { return invoiceId; }
    public StringProperty merchantIdProperty() { return merchantId; }
    public DoubleProperty amountProperty() { return amount; }
    public StringProperty invoiceDateProperty() { return invoiceDate; }
    public StringProperty paymentStatusProperty() { return paymentStatus; }
}