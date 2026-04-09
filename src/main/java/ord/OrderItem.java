package ord;

import javafx.beans.property.*;

public class OrderItem {
    private final IntegerProperty itemId = new SimpleIntegerProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final DoubleProperty unitCost = new SimpleDoubleProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();

    public OrderItem(int itemId, String description, int quantity,
                     double unitCost, double amount) {
        this.itemId.set(itemId);
        this.description.set(description);
        this.quantity.set(quantity);
        this.unitCost.set(unitCost);
        this.amount.set(amount);
    }

    public IntegerProperty itemIdProperty() { return itemId; }
    public StringProperty descriptionProperty() { return description; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty unitCostProperty() { return unitCost; }
    public DoubleProperty amountProperty() { return amount; }

    public int getItemId() { return itemId.get(); }
    public String getDescription() { return description.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getUnitCost() { return unitCost.get(); }
    public double getAmount() { return amount.get(); }
}