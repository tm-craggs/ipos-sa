package ord;

import javafx.beans.property.*;

public class Invoice {
    private final StringProperty invoiceId = new SimpleStringProperty();
    private final StringProperty invoiceDate = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Invoice(String invoiceId, String invoiceDate, double amount, String status) {
        this.invoiceId.set(invoiceId);
        this.invoiceDate.set(invoiceDate);
        this.amount.set(amount);
        this.status.set(status != null ? status : "Pending");
    }

    public String getInvoiceId() { return invoiceId.get(); }
    public String getInvoiceDate() { return invoiceDate.get(); }
    public double getAmount() { return amount.get(); }
    public String getStatus() { return status.get(); }
}