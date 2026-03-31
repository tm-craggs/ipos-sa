package cat;

import javafx.beans.property.*;

public class StockLowLevel {
    private final IntegerProperty ItemId = new SimpleIntegerProperty();
    private final StringProperty Description = new SimpleStringProperty();
    private final IntegerProperty Availability = new SimpleIntegerProperty();
    private final IntegerProperty StockLimit = new SimpleIntegerProperty();
    private final IntegerProperty Recommended = new SimpleIntegerProperty();
    private final DoubleProperty orderPercentage = new SimpleDoubleProperty();

    public StockLowLevel() {}

    public StockLowLevel(int ItemId, String Description, int Availability, int StockLimit, double orderPercentage) {
        this.ItemId.set(ItemId);
        this.Description.set(Description);
        this.Availability.set(Availability);
        this.StockLimit.set(StockLimit);
        this.orderPercentage.set(1 + (orderPercentage / 100.0));
        this.Recommended.set( (int) Math.ceil(StockLimit * this.orderPercentage.get() ) - Availability);
    }
    public StockLowLevel(int ItemId, String Description, int Availability, int StockLimit) {
        this.ItemId.set(ItemId);
        this.Description.set(Description);
        this.Availability.set(Availability);
        this.StockLimit.set(StockLimit);
        this.orderPercentage.set(10);
        this.Recommended.set( (int) Math.ceil(StockLimit *  (1 + (this.orderPercentage.get() / 100.0) ) - Availability));
    }

    public int getItemId() { return ItemId.get(); }
    public IntegerProperty itemIdProperty() { return ItemId; }
    public String getDescription() { return Description.get(); }
    public StringProperty descriptionProperty() { return Description; }
    public int getAvailability() { return Availability.get(); }
    public IntegerProperty availabilityProperty() { return Availability; }
    public int getStockLimit() { return StockLimit.get(); }
    public IntegerProperty stockLimitProperty() { return StockLimit; }
    public int getRecommended() { return Recommended.get(); }
    public IntegerProperty recommendedProperty() { return Recommended; }
    public double getOrderPercentage() { return orderPercentage.get(); }
    public DoubleProperty orderPercentageProperty() { return orderPercentage; }
}
